import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SampleEncryption {

	public static void main(String[] args) {
		DiffieHellmanExchange dhExchange = new DiffieHellmanExchange();
		byte[] myPubKey = dhExchange.generatePublicKey();
		ArrayList<Byte> remotePubKey = new ArrayList<>();
		try {
			Socket clientSock = new Socket("127.0.0.1", 7777);
			DataOutputStream msgStream = new DataOutputStream(clientSock.getOutputStream());
			DataInputStream inputStream = new DataInputStream(clientSock.getInputStream());
			byte[] msgToSend = new byte[myPubKey.length+2];
			msgToSend[0] = 0;
			msgToSend[1] = Constants.TYPE_CREATE;
			for(int i=0;i<myPubKey.length;i++)
				msgToSend[i+2] = myPubKey[i];
			msgStream.write(msgToSend);
			clientSock.shutdownOutput();
			while(true) {
				int inputByte = inputStream.read();
				if(inputByte == -1)
					break;
				remotePubKey.add((byte) inputByte); 
			}
			byte[] message = new byte[remotePubKey.size()];
			for(int i=0; i<remotePubKey.size(); i++)
				message[i] = remotePubKey.get(i);
			byte[] sharedKey = dhExchange.computeSharedKey(message);
			for(int i=0; i<sharedKey.length; i++)
				System.out.println((int)sharedKey[i]);
			AESEncrypter sampleEncrypter = new AESEncrypter(sharedKey);
			String sampleMessage = "hello";
			byte[] encrypted = sampleEncrypter.encryptMessage(sampleMessage.getBytes());
			System.out.println("Encrypted message:");
			System.out.println(new String(encrypted));
			System.out.println("Decrypted message:");
			System.out.println(new String(sampleEncrypter.decryptMessage(encrypted)));
			msgStream.flush();
			msgStream.close();
			inputStream.close();
			clientSock.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
