import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class OnionServer {
	
	private Hashtable<RoutingEntry, byte[]> secretKeys = new Hashtable<>();
	private Hashtable<RoutingEntry, RoutingEntry> routingTable = new Hashtable<>();
	private int receivingPort;
	
	public void updateSecretKeysTable(RoutingEntry newCircuit, byte[] secretKey){
		secretKeys.put(newCircuit, secretKey);
	}
	
	public void updateRoutingTable(String srcAddress, int srcCircuitId, String dstnAddress, int dstnCircuitId){
		routingTable.put(new RoutingEntry(srcAddress, srcCircuitId), new RoutingEntry(dstnAddress, dstnCircuitId));
	}
	
	public RoutingEntry getDestination(RoutingEntry sourceRoutingEntry, byte direction){
		switch(direction){
			case Constants.DIRECTION_FORWARD:
				return routingTable.get(sourceRoutingEntry);
			case Constants.DIRECTION_REVERSE:
				for(RoutingEntry routingEntry: routingTable.keySet()){
					if(routingTable.get(routingEntry).equals(sourceRoutingEntry))
						return routingEntry;
				}
			default:
				return null;
		}
	}
	
	public byte getDirection(RoutingEntry routingEntry){
		if(routingTable.containsKey(routingEntry))
			return Constants.DIRECTION_FORWARD;
		else if(routingTable.containsValue(routingEntry))
			return Constants.DIRECTION_REVERSE;
		
		return -1;
	}
	
	public byte[] getSecretKey(RoutingEntry srcEntry){
		return secretKeys.get(srcEntry);
	}
	
	public void printSecretKeyTable(){
		for(byte[] value : secretKeys.values()){
			for(int i=0;i<value.length;i++)
				System.out.println((int)value[i]);
		}
	}
	
	public void printRoutingTable(){
		System.out.println(routingTable.toString());
	}
	
	private RoutingEntry getSecretKeyRoutingEntry(RoutingEntry srcRoutingEntry, byte messageDirection) {
		if(messageDirection == Constants.DIRECTION_REVERSE)
			return getDestination(srcRoutingEntry, messageDirection);
		
		return srcRoutingEntry;
	}
	
	private void checkTypeAndCreateThread(Socket socket, ArrayList<Byte> messageBytes) throws IOException{
		byte circId = messageBytes.get(Constants.INDEX_CIRCUIT_ID);
		byte[] bytePort = new byte[2];
		bytePort[0] = messageBytes.get(Constants.INDEX_RECEIVING_PORT_START);
		bytePort[1] = messageBytes.get(Constants.INDEX_RECEIVING_PORT_START + 1);
		int sourceReceivingPort = Utils.convertBytePortToInt(bytePort); 
		byte messageType = messageBytes.get(Constants.INDEX_MESSAGE_TYPE);
		byte messageDirection;
		byte[] messageByteArrayWithoutCircIdAndRecvPort = Utils.convertToByteArray(messageBytes, 3);
		byte[] messageByteArrayWithCircIdAndRecvPort;
		String sourceIpAndPort = Utils.getAddressFromSocket(socket, sourceReceivingPort);
		
		System.out.println("message received from " + socket.getRemoteSocketAddress());
		
		if (messageType == Constants.TYPE_CREATE){
			System.out.println("Message type === " + messageType);
			String msgContents = new String(messageByteArrayWithoutCircIdAndRecvPort);
			System.out.println("Message contents:"+msgContents);
//			System.out.println("Starting create thread!");
			(new CreateMessageThread(socket, sourceReceivingPort, messageBytes, OnionServer.this, null)).start();
		} else {
			RoutingEntry srcRoutingEntry = new RoutingEntry(sourceIpAndPort, circId);
			messageDirection = getDirection(srcRoutingEntry);
			RoutingEntry secretKeyRotuingEntry = getSecretKeyRoutingEntry(srcRoutingEntry, messageDirection);
//			System.out.println("Decrypting with key:");
//			for(int i=0;i<getSecretKey(secretKeyRotuingEntry).length;i++)
//				System.out.println((int)getSecretKey(secretKeyRotuingEntry)[i]);
			ArrayList<byte[]> secretKeys = Utils.createDummySecretKeysArrayList(getSecretKey(secretKeyRotuingEntry));
//			System.out.println("Trying to decrypt:");
//			for(int i=0;i<messageByteArrayWithoutCircIdAndRecvPort.length;i++)
//				System.out.println((int)messageByteArrayWithoutCircIdAndRecvPort[i]);
			OnionEncrypter onionEncrypter = new OnionEncrypter(messageByteArrayWithoutCircIdAndRecvPort, secretKeys);
			if(messageDirection != Constants.DIRECTION_REVERSE){
				messageByteArrayWithoutCircIdAndRecvPort = onionEncrypter.decryptLayer();
				String msgContents = new String(messageByteArrayWithoutCircIdAndRecvPort);
				System.out.println("Message contents:"+msgContents);
//				System.out.println("Decrypted!!");
			}
			messageByteArrayWithCircIdAndRecvPort = Utils.addCircIdAndRecvPortIntoByteArray(messageByteArrayWithoutCircIdAndRecvPort, circId, bytePort);
			messageType = messageByteArrayWithCircIdAndRecvPort[Constants.INDEX_MESSAGE_TYPE];
			
			if (messageDirection == Constants.DIRECTION_FORWARD || messageType == Constants.TYPE_EXTEND){
				System.out.println("Message type === " + messageType);
				switch(messageType){
					case Constants.TYPE_EXTEND:
//						System.out.println("Starting extend thread!");
						(new ExtendMessageThread(socket, receivingPort, messageByteArrayWithCircIdAndRecvPort, OnionServer.this)).start();
						break;
					case Constants.TYPE_RELAY:
//						System.out.println("Starting relay thread!");
						(new RelayMessageThread(messageByteArrayWithCircIdAndRecvPort, OnionServer.this, null,
								messageDirection, srcRoutingEntry, receivingPort, true)).start();
						socket.close();
						break;
					case Constants.TYPE_DESTROY:
						socket.close();
						break;
				}
			} else {
//				System.out.println("Encrypting relay with key:");
//				Utils.printByteArray(secretKeys.get(0), 0);
				messageByteArrayWithoutCircIdAndRecvPort = onionEncrypter.encryptLayer();
//				System.out.println("Actual encrypted part:");
//				Utils.printByteArray(messageByteArrayWithoutCircIdAndRecvPort, 0);
				messageByteArrayWithCircIdAndRecvPort = Utils.addCircIdAndRecvPortIntoByteArray(messageByteArrayWithoutCircIdAndRecvPort, circId, bytePort);
				String msgContents = new String(messageByteArrayWithoutCircIdAndRecvPort);
				System.out.println("Message contents:"+msgContents);
				(new RelayMessageThread(messageByteArrayWithCircIdAndRecvPort, OnionServer.this, null,
						messageDirection, srcRoutingEntry, receivingPort, false)).run();
				socket.close();
			}
		}
	}

	public void startServer() {
		ServerSocket serverSocket;
		Socket socket;
		try {
			serverSocket = new ServerSocket(0);
			receivingPort = serverSocket.getLocalPort();
			System.out.println("Onion server started at " + serverSocket.getInetAddress().getHostAddress() + ":" + receivingPort);
			while(true) {
				socket = serverSocket.accept();
				ArrayList<Byte> messageBytes = Utils.readMessageBytes(socket);
				checkTypeAndCreateThread(socket, messageBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		OnionServer os = new OnionServer();
		os.startServer();
	}
}
