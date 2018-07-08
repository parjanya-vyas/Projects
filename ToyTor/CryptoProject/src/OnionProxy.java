import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import javax.swing.JTextArea;

public class OnionProxy {
	private ArrayList<String> onionRouterAddresses;
	private Hashtable<String, byte[]> secretKeys = new Hashtable<>();
	
	private DiffieHellmanExchange dhExchange = new DiffieHellmanExchange();
	
	private String ownIpAddress = null;
	private String destinationIpAddress;
	
	private int destinationPort;
	private int ownReceivingPort = -1;
	
	private byte circId;
	
	CountDownLatch extendedLatch;
	
	JTextArea chatHistory;
	
	/*public OnionProxy(JTextArea chatHistory) {
		this.chatHistory = chatHistory;
	}*/
	
	private class OnionProxyListener implements Runnable {

		@Override
		public void run() {
			ServerSocket serverSocket;
			Socket socket;
			try {
				serverSocket = new ServerSocket(0);
				ownReceivingPort = serverSocket.getLocalPort();
				ownIpAddress = serverSocket.getInetAddress().getHostAddress();
				
				System.out.println("Onion proxy listening at " + ownIpAddress + ":" + ownReceivingPort);
				while(true) {
					socket = serverSocket.accept();
					ArrayList<Byte> messageBytes = Utils.readMessageBytes(socket);
					socket.close();
					boolean isTypeExtended = false;
					byte[] decryptedMsg = Utils.convertToByteArray(messageBytes, 3);
					
					for(int i=0;i<onionRouterAddresses.size();i++){
						OnionEncrypter onionEncrypter = new OnionEncrypter(decryptedMsg,
								Utils.createDummySecretKeysArrayList(secretKeys.get(onionRouterAddresses.get(i))));
						decryptedMsg = onionEncrypter.decryptLayer();
						byte[] remotePublicKey;
						if(decryptedMsg[0]==Constants.TYPE_EXTENDED){
							isTypeExtended = true;
							remotePublicKey = Arrays.copyOfRange(decryptedMsg, 1, decryptedMsg.length);
							byte[] secretKey = dhExchange.computeSharedKey(remotePublicKey);
							if(i+1 == onionRouterAddresses.size()) {
								secretKeys.put(destinationIpAddress + ":" + destinationPort, secretKey);							
							} else {
								secretKeys.put(onionRouterAddresses.get(i+1), secretKey);
							}
							extendedLatch.countDown();
							break;
						}
					}
					if(!isTypeExtended) {
						OnionEncrypter onionEncrypter = new OnionEncrypter(decryptedMsg,
								Utils.createDummySecretKeysArrayList(secretKeys.get(destinationIpAddress + ":" + destinationPort)));
						decryptedMsg = onionEncrypter.decryptLayer();
						String bobMsg = new String(decryptedMsg);
//						System.out.println("Bob's message: " + bobMsg);
						//to add code for sending this bob's message to alice UI
						//AliceUI alice = new AliceUI();
						//alice.addToHistory(bobMsg);
//						System.out.println(chatHistory.getText());
						chatHistory.setText(chatHistory.getText() + "Bob: " + bobMsg + "\n");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void send(String message, JTextArea chatHistory) {
		this.chatHistory = chatHistory;
		byte[] messageByteArray;
		byte[] messageToSendByteArray;
		messageByteArray = message.getBytes();
		messageToSendByteArray = new byte[messageByteArray.length + 1];
		messageToSendByteArray[0] = Constants.TYPE_RELAY;
		for(int j=0; j<messageByteArray.length; j++) {
			messageToSendByteArray[j+1] = messageByteArray[j];
		}
		OnionEncrypter onionEncrypter = new OnionEncrypter(messageToSendByteArray, 
				Utils.createDummySecretKeysArrayList(secretKeys.get(destinationIpAddress + ":" + destinationPort)));
		
		messageByteArray = onionEncrypter.encryptLayer();
		
		for(int i=onionRouterAddresses.size()-1; i>=0; i--) {
			messageToSendByteArray = new byte[messageByteArray.length + 1];
			messageToSendByteArray[0] = Constants.TYPE_RELAY;
			for(int j=0; j<messageByteArray.length; j++) {
				messageToSendByteArray[j+1] = messageByteArray[j];
			}
			onionEncrypter = new OnionEncrypter(messageToSendByteArray, 
					Utils.createDummySecretKeysArrayList(secretKeys.get(onionRouterAddresses.get(i))));
			
			messageByteArray = onionEncrypter.encryptLayer();
		}
		
		messageToSendByteArray = new byte[messageByteArray.length + 3];
		messageToSendByteArray[0] = circId;
		byte[] recvPortBytes = Utils.convertIntPortToByte(ownReceivingPort);
		messageToSendByteArray[1] = recvPortBytes[0];
		messageToSendByteArray[2] = recvPortBytes[1];
		for(int j=0; j<messageByteArray.length; j++) {
			messageToSendByteArray[j+3] = messageByteArray[j];
		}
		
		try {
			Utils.sendMessageBytes(Utils.getIpFromIpPortString(onionRouterAddresses.get(0)), 
					Utils.getPortFromIpPortString(onionRouterAddresses.get(0)), 
					messageToSendByteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendCreateMessage(String dstnAddress){
		try {
			DiffieHellmanExchange dhTempExchange = new DiffieHellmanExchange();
			byte[] myPublicKey = dhTempExchange.generatePublicKey();
			byte[] remotePublicKey;
			byte[] computedSecretKey;
			circId = (byte) Utils.getNewConnectionCircuitId(ownIpAddress + ":" + ownReceivingPort);
			ArrayList<Byte> createPacket = Utils.constructCreateMessagePacket(circId,
					Utils.convertIntPortToByte(ownReceivingPort), myPublicKey);
			remotePublicKey = Utils.sendAndReceiveMessageBytes(dstnAddress, Utils.convertToByteArray(createPacket, 0));
			computedSecretKey = dhTempExchange.computeSharedKey(remotePublicKey);
			
			secretKeys.put(dstnAddress, computedSecretKey);
			System.out.println("circuit created with " + dstnAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendExtendMessage(String lastNodeOfCurrentCircuit, String newNodeAddress){
		try {
			byte[] myPublicKey = dhExchange.generatePublicKey();
			ArrayList<Byte> extendPacket = Utils.constructExtendMessagePacket(circId,
					Utils.convertIntPortToByte(ownReceivingPort),
					Utils.convertStringIpToBytes(Utils.getIpFromIpPortString(newNodeAddress)), 
					Utils.convertIntPortToByte(Utils.getPortFromIpPortString(newNodeAddress)),
					secretKeys.get(lastNodeOfCurrentCircuit),
					myPublicKey);
			Utils.sendMessageBytes(Utils.getIpFromIpPortString(lastNodeOfCurrentCircuit),
					Utils.getPortFromIpPortString(lastNodeOfCurrentCircuit), 
					Utils.convertToByteArray(extendPacket, 0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendRelayMessage(String dstnAddress, byte[] messageBytes){
		ArrayList<Byte> relayPacket = Utils.constructRelayMessagePacket(circId,
				Utils.convertIntPortToByte(ownReceivingPort),
				messageBytes,
				secretKeys.get(dstnAddress));
		try {
			Utils.sendMessageBytes(Utils.getIpFromIpPortString(dstnAddress), 
					Utils.getPortFromIpPortString(dstnAddress),
					Utils.convertToByteArray(relayPacket, 0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createNewCircuit(ArrayList<String> onionRouterAddresses, String destinationIpAddress,
			int destinationPort) throws InterruptedException{
		this.onionRouterAddresses = onionRouterAddresses;
		this.destinationIpAddress = destinationIpAddress;
		this.destinationPort = destinationPort;
		
		sendCreateMessage(onionRouterAddresses.get(0));
		sendExtendMessage(onionRouterAddresses.get(0), onionRouterAddresses.get(1));
		
		System.out.println("Waiting for extend reply!");
		
		while(extendedLatch.getCount()!=2);
		System.out.println("circuit created with " + onionRouterAddresses.get(1));
		dhExchange = new DiffieHellmanExchange();
		byte[] newPublicKey = dhExchange.generatePublicKey();
		ArrayList<Byte> extendPayloadArrayList = Utils.constructExtendMessagePacket(circId,
				Utils.convertIntPortToByte(ownReceivingPort),
				Utils.convertStringIpToBytes(Utils.getIpFromIpPortString(onionRouterAddresses.get(2))), 
				Utils.convertIntPortToByte(Utils.getPortFromIpPortString(onionRouterAddresses.get(2))),
				secretKeys.get(onionRouterAddresses.get(1)),
				newPublicKey);
		byte[] extendPayloadByteArray = Utils.convertToByteArray(extendPayloadArrayList, 3);
		System.out.println("Sending relay message!");
		sendRelayMessage(onionRouterAddresses.get(0), extendPayloadByteArray);
		
		System.out.println("Waiting for relay reply");
		
		while(extendedLatch.getCount()!=1);
		System.out.println("circuit created with " + onionRouterAddresses.get(2));
		dhExchange = new DiffieHellmanExchange();
		byte[] bobPublicKey = dhExchange.generatePublicKey(); 
		ArrayList<Byte> bobExtendPayloadArrayList = Utils.constructExtendMessagePacket(circId,
				Utils.convertIntPortToByte(ownReceivingPort),
				Utils.convertStringIpToBytes(destinationIpAddress), 
				Utils.convertIntPortToByte(destinationPort),
				secretKeys.get(onionRouterAddresses.get(2)),
				bobPublicKey);
		byte[] bobExtendPayloadByteArray = Utils.convertToByteArray(bobExtendPayloadArrayList, 3);
		
		ArrayList<Byte> bobRelayPayload = Utils.constructRelayMessagePacket(circId,
				Utils.convertIntPortToByte(ownReceivingPort),
				bobExtendPayloadByteArray,
				secretKeys.get(onionRouterAddresses.get(1)));
		byte[] bobRelayPayloadByteArray = Utils.convertToByteArray(bobRelayPayload, 3);
		sendRelayMessage(onionRouterAddresses.get(0), bobRelayPayloadByteArray);
		
		extendedLatch.await();
		System.out.println("circuit created with " + destinationIpAddress + ":" + destinationPort);
		System.out.println("Complete Circuit is created.......");
	}
	
	public void startServer() {
		extendedLatch = new CountDownLatch(3);
		(new Thread(new OnionProxyListener())).start();
	}
}
