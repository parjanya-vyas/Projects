import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class CreateMessageThread extends Thread{
	Socket socket;
	int sourceReceivingPort;
	ArrayList<Byte> messageBytes;
	DiffieHellmanExchange dhExchange;
	OnionServer onionServer;
	PublicServer publicServer;
	
	public CreateMessageThread(Socket socket, int sourceReceivingPort,  ArrayList<Byte> messageBytes, OnionServer onionServer, PublicServer publicServer) {
		this.socket = socket;
		this.sourceReceivingPort = sourceReceivingPort;
		this.messageBytes = messageBytes;
		this.onionServer = onionServer;
		this.publicServer = publicServer;
		dhExchange = new DiffieHellmanExchange();
	}
	
	@Override
	public void run() {
		byte[] remotePublicKey = Utils.convertToByteArray(messageBytes, 
				Constants.INDEX_RECEIVING_PORT_START + Constants.SIZE_RECEIVING_PORT_BYTES);
		try{
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			byte[] newPublicKey = dhExchange.generatePublicKey();
			//System.out.println("Newely created public key:");
			//Utils.printByteArray(newPublicKey, 0);
			outputStream.write(newPublicKey);
			outputStream.flush();
			byte[] sharedKey = dhExchange.computeSharedKey(remotePublicKey);
			//System.out.println("Storing key for " + Utils.getAddressFromSocket(socket, sourceReceivingPort)+" "+(int)messageBytes.get(Constants.INDEX_CIRCUIT_ID));
			RoutingEntry newCircuit = new RoutingEntry(Utils.getAddressFromSocket(socket, sourceReceivingPort),
					(int)messageBytes.get(Constants.INDEX_CIRCUIT_ID));
			if(onionServer != null)
				onionServer.updateSecretKeysTable(newCircuit, sharedKey);
			else
				publicServer.updateSecretKeysTable(newCircuit, sharedKey);
			outputStream.close();
			socket.close();
			/*System.out.println("Hash table:");
			if(onionServer != null)
				onionServer.printSecretKeyTable();
			else
				publicServer.printSecretKeyTable();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
