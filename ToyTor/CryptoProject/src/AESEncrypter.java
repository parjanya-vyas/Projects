import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypter {
	
	SecretKeySpec secretKeySpec;
	IvParameterSpec ivParameterSpec;
	Cipher AESCipher;
	
	public AESEncrypter(byte[] secretKey) {
		try {
			MessageDigest hashFunction = MessageDigest.getInstance("SHA-256");
			secretKey = hashFunction.digest(secretKey);
			secretKey = Arrays.copyOf(secretKey, 16);
			secretKeySpec = new SecretKeySpec(secretKey, "AES");
			ivParameterSpec = new IvParameterSpec(Constants.IV);
			AESCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] encryptMessage(byte[] message){
		try{
			AESCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			return AESCipher.doFinal(message);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] decryptMessage(byte[] message){
		try{
			AESCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			return AESCipher.doFinal(message);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
