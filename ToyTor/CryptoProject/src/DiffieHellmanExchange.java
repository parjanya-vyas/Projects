import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

public class DiffieHellmanExchange {

		private KeyPair keyPair;
		private KeyAgreement keyAgreement;
		
		private static final byte P_BYTE[] = {
				(byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7,
				(byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7,
	            (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7,
				(byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7,
	            (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7,
				(byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7,
	            (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7,
				(byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
	            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7	            
		};
		
		private static final BigInteger P = new BigInteger(1, P_BYTE);
		private static final BigInteger G = BigInteger.valueOf(2);
		
		public byte[] generatePublicKey() {
			DHParameterSpec dhParameterSpec;
			
			try {
				dhParameterSpec = new DHParameterSpec(P, G);
				KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman");
				keyPairGenerator.initialize(dhParameterSpec);
				keyPair = keyPairGenerator.generateKeyPair();
				
				keyAgreement = KeyAgreement.getInstance("DiffieHellman");
				keyAgreement.init(keyPair.getPrivate());
				
				BigInteger pubKeyBI = ((DHPublicKey) keyPair.getPublic()).getY();
				byte[] publicKeyBytes = pubKeyBI.toByteArray();
				return publicKeyBytes;
				
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public byte[] computeSharedKey(byte[] publicKeyBytes) {
			if(keyAgreement == null)
				return null;
			
			try {
				KeyFactory keyFactory = KeyFactory.getInstance("DiffieHellman");
				BigInteger pubKeyBI = new BigInteger(1, publicKeyBytes);
				
				PublicKey publicKey = keyFactory.generatePublic(new DHPublicKeySpec(pubKeyBI, P, G));
				keyAgreement.doPhase(publicKey, true);
				byte[] sharedKeyBytes = keyAgreement.generateSecret();
				return sharedKeyBytes;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
}
