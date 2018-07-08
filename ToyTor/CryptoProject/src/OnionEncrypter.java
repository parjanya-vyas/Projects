import java.util.ArrayList;

public class OnionEncrypter {
	
	byte[] currentMessageBytes;
	ArrayList<byte[]> sharedKeys;
	
	public OnionEncrypter(byte[] currentMessageBytes, ArrayList<byte[]> sharedKeys) {
		this.currentMessageBytes = currentMessageBytes;
		this.sharedKeys = sharedKeys;
	}
	
	public byte[] encryptLayer(){
		if(sharedKeys.isEmpty())
			return null;
		AESEncrypter aesEncrypter = new AESEncrypter(sharedKeys.get(0));
		return aesEncrypter.encryptMessage(currentMessageBytes);
	}
	
	public byte[] decryptLayer(){
		if(sharedKeys.isEmpty())
			return null;
		AESEncrypter aesEncrypter = new AESEncrypter(sharedKeys.get(0));
		return aesEncrypter.decryptMessage(currentMessageBytes);
	}
	
	public byte[] onionDecrypt(){
		for(int i=0;i<sharedKeys.size();i++){
			currentMessageBytes = decryptLayer();
			sharedKeys.remove(0);
		}
		
		return currentMessageBytes;
	}
}