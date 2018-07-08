import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
	
	private static HashMap<String, Integer> uniqueConnectionCircuitIds = new HashMap<>(); 
	
	public static ArrayList<Byte> readMessageBytes(Socket socket) throws IOException{
		DataInputStream inputStream = new DataInputStream(socket.getInputStream());
		ArrayList<Byte> messageBytes = new ArrayList<>();
		while(true) {
			int inputByte = inputStream.read();
			if(inputByte == -1)
				break;
			messageBytes.add((byte) inputByte); 
		}
		
		return messageBytes;
	}
	
	public static void sendMessageBytes(String inetAddress, int port, byte[] messageByteArray) throws UnknownHostException, IOException{
		Socket socket = new Socket(inetAddress, port);
		DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
		outputStream.write(messageByteArray);
		outputStream.flush();
		outputStream.close();
		socket.close();
	}
	
	public static byte[] sendAndReceiveMessageBytes(String dstnAddress, byte[] msgByteArrayToSend) throws UnknownHostException, IOException{
		Socket socket = new Socket(getIpFromIpPortString(dstnAddress), getPortFromIpPortString(dstnAddress));
		DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
		DataInputStream inputStream = new DataInputStream(socket.getInputStream());
		ArrayList<Byte> receivedMsg = new ArrayList<>();
		outputStream.write(msgByteArrayToSend);
		socket.shutdownOutput();
		while(true) {
			int inputByte = inputStream.read();
			if(inputByte == -1)
				break;
			receivedMsg.add((byte) inputByte); 
		}
		outputStream.close();
		inputStream.close();
		socket.close();
		return convertToByteArray(receivedMsg, 0);
	}
	
	public static int getNewConnectionCircuitId(String ownAddress){
		if(uniqueConnectionCircuitIds.containsKey(ownAddress)){
			int newId = uniqueConnectionCircuitIds.get(ownAddress);
			uniqueConnectionCircuitIds.put(ownAddress, newId+1);
			
			return newId;
		}
		
		uniqueConnectionCircuitIds.put(ownAddress, 1);
		
		return 0;
	}
	
	public static byte[] convertToByteArray(ArrayList<Byte> inputArrayList, int startIndex){
		byte[] resultantByteArray = new byte[inputArrayList.size()-startIndex];
		for(int i=startIndex;i<inputArrayList.size();i++)
			resultantByteArray[i-startIndex] = inputArrayList.get(i);
		
		return resultantByteArray;
	}
	
	public static ArrayList<Byte> convertToByteArrayList(byte[] inputArray, int startIndex){
		ArrayList<Byte> resultantByteArrayList = new ArrayList<>();
		for(int i=startIndex;i<inputArray.length;i++)
			resultantByteArrayList.add(inputArray[i]);
		
		return resultantByteArrayList;
	}
	
	public static ArrayList<byte[]> createDummySecretKeysArrayList(byte[] secretKey){
		ArrayList<byte[]> dummyArrayList = new ArrayList<>();
		dummyArrayList.add(secretKey);
		
		return dummyArrayList;
	}
	
	public static byte[] removeFirstByteFromByteArray(byte[] messageByteArray){
		byte[] resultantByteArray = new byte[messageByteArray.length-1];
		for(int i=0;i<messageByteArray.length-1;i++)
			resultantByteArray[i] = messageByteArray[i+1];
		
		return resultantByteArray;
	}
	
	public static byte[] addCircIdAndRecvPortIntoByteArray(byte[] messageByteArray, byte circId, byte[] recvPort){
		byte[] resultantByteArray = new byte[messageByteArray.length+1+recvPort.length];
		resultantByteArray[0] = circId;
		resultantByteArray[1] = recvPort[0];
		resultantByteArray[2] = recvPort[1];
		for(int i=3;i<resultantByteArray.length;i++)
			resultantByteArray[i] = messageByteArray[i-3];
		
		return resultantByteArray;
	}
	
	public static String getAddressFromSocket(Socket socket, int receivingPort){
		return (socket.getInetAddress().getHostAddress() + ":" + receivingPort);
	}
	
	public static String getIpFromIpPortString(String IpPort){
		String[] ipPortArray = IpPort.split(":");
		return ipPortArray[0];
	}
	
	public static int getPortFromIpPortString(String IpPort){
		String[] ipPortArray = IpPort.split(":");
		return Integer.parseInt(ipPortArray[1]);
	}
	
	public static int convertBytePortToInt(byte[] bytePort){
		return ((bytePort[0] & 0xff) << 8) | (bytePort[1] & 0xff);
	}
	
	public static byte[] convertIntPortToByte(int intPort){
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.putInt(intPort);
		
		byte[] bytePort = new byte[2];
		bytePort[0] = byteBuffer.array()[2];
		bytePort[1] = byteBuffer.array()[3];

		return bytePort;
	}
	
	public static byte[] getPortFromMessageByteArray(byte[] messageByteArray, int startIndex){
		byte[] portBytes = new byte[2];
		portBytes[0] = messageByteArray[startIndex];
		portBytes[1] = messageByteArray[startIndex + 1];
		
		return portBytes;
	}
	
	public static byte[] convertStringIpToBytes(String ipString){
		byte[] ipBytes = new byte[4];
		String[] ipArray = ipString.split("\\.");
		for(int i=0;i<Constants.SIZE_IP_BYTES;i++)
			ipBytes[i] = (byte)(Integer.parseInt(ipArray[i]));
		
		return ipBytes;
	}
	
	public static String getIpStringFromMessageByteArray(byte[] messageByteArray, int startIndex){
		String ipString;
		int ipValue = (0xFF & messageByteArray[startIndex]);
		ipString = "" + ipValue;
		for(int i = startIndex+1; i<(startIndex + Constants.SIZE_IP_BYTES); i++) {
			ipValue = (0xFF & messageByteArray[i]);
			ipString = ipString + "." + ipValue;
		}
		
		return ipString;
	}
	
	public static ArrayList<Byte> constructCreateMessagePacket(byte circId, byte[] ownRecvPort, byte[] myPublicKey){
		ArrayList<Byte> createPacket = new ArrayList<>();
		createPacket.add(circId);
		createPacket.addAll(Utils.convertToByteArrayList(ownRecvPort, 0));
		createPacket.add(Constants.TYPE_CREATE);
		createPacket.addAll(Utils.convertToByteArrayList(myPublicKey, 0));
		
		return createPacket;
	}
	
	public static ArrayList<Byte> constructExtendMessagePacket(byte circId, byte[] ownRecvPort,
			byte[] dstnIp, byte[] dstnPort, byte[] secretKey, byte[] myPublicKey){
		ArrayList<Byte> extendMessagePacket = new ArrayList<>();
		extendMessagePacket.add(Constants.TYPE_EXTEND);
		extendMessagePacket.addAll(convertToByteArrayList(dstnIp, 0));
		extendMessagePacket.addAll(convertToByteArrayList(dstnPort, 0));
		extendMessagePacket.addAll(convertToByteArrayList(myPublicKey, 0));
		
		byte[] msgBytesWithoutCircIdAndRecvPort = convertToByteArray(extendMessagePacket, 0);
		OnionEncrypter onionEncrypter = new OnionEncrypter(msgBytesWithoutCircIdAndRecvPort,
				createDummySecretKeysArrayList(secretKey));
		byte[] encryptedMsgBytesWithoutCircIdAndRecvPort = onionEncrypter.encryptLayer();
		
		ArrayList<Byte> encryptedExtendPacket = new ArrayList<>();
		encryptedExtendPacket.add(circId);
		encryptedExtendPacket.addAll(convertToByteArrayList(ownRecvPort, 0));
		encryptedExtendPacket.addAll(convertToByteArrayList(encryptedMsgBytesWithoutCircIdAndRecvPort, 0));
		
		return encryptedExtendPacket;
	}
	
	public static ArrayList<Byte> constructRelayMessagePacket(byte circId, byte[] ownRecvPort,
			byte[] msgBytes, byte[] secretKey){
		ArrayList<Byte> relayMessagePacket = new ArrayList<>();
		relayMessagePacket.add(Constants.TYPE_RELAY);
		relayMessagePacket.addAll(convertToByteArrayList(msgBytes, 0));
		
		byte[] msgBytesWithoutCircIdAndRecvPort = convertToByteArray(relayMessagePacket, 0);
		OnionEncrypter onionEncrypter = new OnionEncrypter(msgBytesWithoutCircIdAndRecvPort,
				createDummySecretKeysArrayList(secretKey));
		byte[] encryptedMsgBytesWithoutCircIdAndRecvPort = onionEncrypter.encryptLayer();
		
		ArrayList<Byte> encryptedRelayPacket = new ArrayList<>();
		encryptedRelayPacket.add(circId);
		encryptedRelayPacket.addAll(convertToByteArrayList(ownRecvPort, 0));
		encryptedRelayPacket.addAll(convertToByteArrayList(encryptedMsgBytesWithoutCircIdAndRecvPort, 0));
		
		return encryptedRelayPacket;
	}
	
	public static void printByteArray(byte[] byteArray, int startIndex){
		for(int i=startIndex;i<byteArray.length;i++)
			System.out.println((int)byteArray[i]);
	}
}
