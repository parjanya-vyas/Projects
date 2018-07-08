import java.io.IOException;

public class RelayMessageThread extends Thread{
	byte[] messageByteArray;
	OnionServer onionServer;
	PublicServer publicServer;
	RoutingEntry srcRoutingEntry;
	int ownRecvPort;
	byte messageDirection;
	boolean isTypeFieldIncludedInMsg;
	
	public RelayMessageThread(byte[] messageByteArray, OnionServer onionServer, PublicServer publicServer,
			byte messageDirection, RoutingEntry srcRoutingEntry, int ownRecvPort, boolean isTypeFieldIncludedInMsg) {
		this.messageByteArray = messageByteArray;
		this.onionServer = onionServer;
		this.publicServer = publicServer;
		this.messageDirection = messageDirection;
		this.srcRoutingEntry = srcRoutingEntry;
		this.ownRecvPort = ownRecvPort;
		this.isTypeFieldIncludedInMsg = isTypeFieldIncludedInMsg;
	}
	
	private String getIpFromRoutingEntry(RoutingEntry routingEntry){
		return Utils.getIpFromIpPortString(routingEntry.getAddr());
	}
	
	private int getPortFromRoutingEntry(RoutingEntry routingEntry){
		return Utils.getPortFromIpPortString(routingEntry.getAddr());
	}
	
	@Override
	public void run() {
		RoutingEntry dstnRoutingEntry;
		if(onionServer != null)
			dstnRoutingEntry = onionServer.getDestination(srcRoutingEntry, messageDirection);
		else
			dstnRoutingEntry = publicServer.getDestination(srcRoutingEntry, messageDirection);
		
		byte[] messageBytesToSend;
		if(isTypeFieldIncludedInMsg)
			messageBytesToSend = new byte[messageByteArray.length-1];
		else
			messageBytesToSend = new byte[messageByteArray.length];
		
		byte[] ownRecvPortBytes = Utils.convertIntPortToByte(ownRecvPort);
		messageBytesToSend[Constants.INDEX_CIRCUIT_ID] = (byte)dstnRoutingEntry.getCircId();
		messageBytesToSend[Constants.INDEX_RECEIVING_PORT_START] = ownRecvPortBytes[0];
		messageBytesToSend[Constants.INDEX_RECEIVING_PORT_START + 1] = ownRecvPortBytes[1];
		for(int i=Constants.INDEX_MESSAGE_TYPE;i<messageBytesToSend.length;i++){
			if(isTypeFieldIncludedInMsg)
				messageBytesToSend[i] = messageByteArray[i+1];
			else
				messageBytesToSend[i] = messageByteArray[i];
		}
//		System.out.println("Relaying encrypted message:");
//		Utils.printByteArray(messageBytesToSend, 3);
		try {
			Utils.sendMessageBytes(getIpFromRoutingEntry(dstnRoutingEntry), getPortFromRoutingEntry(dstnRoutingEntry),
					messageBytesToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
