import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ExtendMessageThread extends Thread{
	Socket socket;
	byte[] messageByteArray;
	ArrayList<byte[]> sharedKey;
	OnionServer onionServer;
	int ownReceivingPort;
	
	public ExtendMessageThread(Socket socket, int receivingPort ,byte[] messageByteArray, OnionServer onionServer) {
		this.socket = socket;
		this.messageByteArray = messageByteArray;
		this.onionServer = onionServer;
		this.ownReceivingPort = receivingPort;
	}
	
	@Override
	public void run() {
		byte[] srcRecvBytePort = Utils.getPortFromMessageByteArray(messageByteArray,
				Constants.INDEX_RECEIVING_PORT_START);
		int sourceReceivingPort = Utils.convertBytePortToInt(srcRecvBytePort); 
		byte[] ownRecvPortBytes = Utils.convertIntPortToByte(ownReceivingPort);
		
		int newCircuitId = Utils.getNewConnectionCircuitId(socket.getLocalAddress().getHostAddress() + ":" + ownReceivingPort);
		String srcAddr = Utils.getAddressFromSocket(socket, sourceReceivingPort);
		int srcCircuitId = messageByteArray[Constants.INDEX_CIRCUIT_ID];
		String nextHopIP = Utils.getIpStringFromMessageByteArray(messageByteArray, Constants.INDEX_IP_START);
		
		byte[] nextHopBytePort = Utils.getPortFromMessageByteArray(messageByteArray,
				Constants.INDEX_PORT_START);
		int nextHopPort = Utils.convertBytePortToInt(nextHopBytePort);

		String dstnAddr = nextHopIP + ":" + nextHopPort;
		onionServer.updateRoutingTable(srcAddr, srcCircuitId, dstnAddr, newCircuitId);
		
		ArrayList<Byte> createMessageBytes = Utils.constructCreateMessagePacket((byte) newCircuitId,
				ownRecvPortBytes,
				Arrays.copyOfRange(messageByteArray, 10, messageByteArray.length));
		
		byte[] createMessageByteArray = Utils.convertToByteArray(createMessageBytes, 0);
		
		try {
			byte[] reply = Utils.sendAndReceiveMessageBytes(dstnAddr, createMessageByteArray);
			
			/*System.out.println("New public key:");
			for(int i=0;i<reply.length;i++)
				System.out.println((int)reply[i]);*/
			
//			System.out.println("Sending it to" + srcAddr);
			
			byte[] replyMessage = new byte[reply.length + 1];
			replyMessage[0] = Constants.TYPE_EXTENDED;
			for(int i=0; i<reply.length; i++)
				replyMessage[i+1] = reply[i];
			
//			System.out.println("Encrypting with key of:"+srcAddr+" "+srcCircuitId);
			
			OnionEncrypter onionEnc = new OnionEncrypter(replyMessage, Utils.createDummySecretKeysArrayList(onionServer.getSecretKey(new RoutingEntry(srcAddr, srcCircuitId))));
			byte[] encryptedMsgWithoutCircuitIdAndRecvPort = onionEnc.encryptLayer();
			byte[] messageWithCircuitIdAndRecvPort = new byte[encryptedMsgWithoutCircuitIdAndRecvPort.length + 3];
			messageWithCircuitIdAndRecvPort[0] = (byte) srcCircuitId;
			
			messageWithCircuitIdAndRecvPort[1] = ownRecvPortBytes[0];
			messageWithCircuitIdAndRecvPort[2] = ownRecvPortBytes[1];
//			System.out.println("Encrypted extended message:");
//			Utils.printByteArray(encryptedMsgWithoutCircuitIdAndRecvPort, 0);
			for(int i=0; i<encryptedMsgWithoutCircuitIdAndRecvPort.length; i++)
				messageWithCircuitIdAndRecvPort[i+3] = encryptedMsgWithoutCircuitIdAndRecvPort[i];
			
			Utils.sendMessageBytes(Utils.getIpFromIpPortString(srcAddr), Utils.getPortFromIpPortString(srcAddr), messageWithCircuitIdAndRecvPort);
			socket.close();
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
