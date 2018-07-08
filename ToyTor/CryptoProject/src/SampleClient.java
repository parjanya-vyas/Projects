import java.io.DataOutputStream;
import java.net.Socket;

public class SampleClient {
	public static void main(String[] args){
		try {
			Socket clientSock = new Socket(args[0], Integer.parseInt(args[1]));
			DataOutputStream msgStream = new DataOutputStream(clientSock.getOutputStream());
			msgStream.writeUTF("Hello!");
			msgStream.flush();
			msgStream.close();
			clientSock.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
