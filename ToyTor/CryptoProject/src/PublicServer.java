import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;

public class PublicServer extends Thread{
	
	private Hashtable<RoutingEntry, byte[]> secretKeys = new Hashtable<>();
	private int receivingPort;
	String dstnIp;
	int sourceReceivingPort;
	Scanner sc;
	
	BobUI bob;
	
	public PublicServer() {
		bob = new BobUI();
		bob.createFrame();
	}
	
	public void updateSecretKeysTable(RoutingEntry newCircuit, byte[] secretKey){
		secretKeys.put(newCircuit, secretKey);
	}
	
	public void printSecretKeyTable(){
		for(byte[] value : secretKeys.values()){
			for(int i=0;i<value.length;i++)
				System.out.println((int)value[i]);
		}
	}
	
	public byte[] getSecretKey(RoutingEntry srcEntry){
		return secretKeys.get(srcEntry);
	}
	
	public RoutingEntry getDestination(RoutingEntry sourceRoutingEntry, byte direction){
		return (new RoutingEntry(dstnIp + ":" + sourceReceivingPort, sourceRoutingEntry.getCircId()));
	}
	
	private void checkTypeAndCreateThread(Socket socket, ArrayList<Byte> messageBytes){
		byte circId = messageBytes.get(Constants.INDEX_CIRCUIT_ID);
		byte[] bytePort = new byte[2];
		bytePort[0] = messageBytes.get(Constants.INDEX_RECEIVING_PORT_START);
		bytePort[1] = messageBytes.get(Constants.INDEX_RECEIVING_PORT_START + 1);
		sourceReceivingPort = Utils.convertBytePortToInt(bytePort); 
		byte messageType = messageBytes.get(Constants.INDEX_MESSAGE_TYPE);
		byte[] messageByteArrayWithoutCircIdAndRecvPort = Utils.convertToByteArray(messageBytes, 3);
		byte[] messageByteArrayWithCircIdAndRecvPort;
		String sourceIpAndPort = Utils.getAddressFromSocket(socket, sourceReceivingPort);
		
		System.out.println("message received from " + socket.getRemoteSocketAddress());
		
		if (messageType == Constants.TYPE_CREATE){
			System.out.println("Message type === " + messageType);
			(new CreateMessageThread(socket, sourceReceivingPort, messageBytes, null, PublicServer.this)).run();
		} else {
			RoutingEntry srcRoutingEntry = new RoutingEntry(sourceIpAndPort, circId);
			ArrayList<byte[]> secretKeys = Utils.createDummySecretKeysArrayList(getSecretKey(srcRoutingEntry));
			OnionEncrypter onionEncrypter = new OnionEncrypter(messageByteArrayWithoutCircIdAndRecvPort, secretKeys);
			messageByteArrayWithoutCircIdAndRecvPort = onionEncrypter.decryptLayer();
			byte[] aliceMsgBytes = Arrays.copyOfRange(messageByteArrayWithoutCircIdAndRecvPort, 1, messageByteArrayWithoutCircIdAndRecvPort.length);
			String aliceMsgString = new String(aliceMsgBytes);
			
			bob.addToHistory(aliceMsgString);
			
//			System.out.println("Alice said: " + aliceMsgString);
			String bobMessage = bob.getMessageFromTextarea();
//			System.out.println("Bob's message: " + bobMessage);
			
			onionEncrypter = new OnionEncrypter(bobMessage.getBytes(), secretKeys);
			messageByteArrayWithoutCircIdAndRecvPort = onionEncrypter.encryptLayer();
			messageByteArrayWithCircIdAndRecvPort = Utils.addCircIdAndRecvPortIntoByteArray(messageByteArrayWithoutCircIdAndRecvPort, circId,
					Utils.convertIntPortToByte(receivingPort));
				
			(new RelayMessageThread(messageByteArrayWithCircIdAndRecvPort, null, PublicServer.this,
								Constants.DIRECTION_REVERSE, srcRoutingEntry, receivingPort, false)).run();
			
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
		}
	}
	
	@Override
	public void run() {
		ServerSocket serverSocket;
		Socket socket;
		try {
			sc = new Scanner(System.in);
			serverSocket = new ServerSocket(0);
			receivingPort = serverSocket.getLocalPort();
			System.out.println("Public server started at " + serverSocket.getInetAddress().getHostAddress() + ":" + receivingPort);
			bob.label.setText("Server started at: " + serverSocket.getInetAddress().getHostAddress() + ":" + receivingPort);
			while(true) {
				socket = serverSocket.accept();
				dstnIp = socket.getInetAddress().getHostName();
				ArrayList<Byte> messageBytes = Utils.readMessageBytes(socket);
				checkTypeAndCreateThread(socket, messageBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
	
	public static void main(String[] args) {
		PublicServer publicServer = new PublicServer();
		publicServer.start();
	}
}
