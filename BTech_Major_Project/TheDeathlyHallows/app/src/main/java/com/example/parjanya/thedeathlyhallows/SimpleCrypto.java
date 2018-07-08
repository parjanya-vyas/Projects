package com.example.parjanya.thedeathlyhallows;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Parjanya on 4/26/2015.
 */
public class SimpleCrypto {

    private final static String HEX = "0123456789ABCDEF";

    public static String encrypt(String seed, String plainText)throws Exception{
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] cipher = encrypt(rawKey,plainText.getBytes());
        return toHex(cipher);
    }

    public static String decrypt(String seed, String cipherText)throws Exception{
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] encrypted = toByte(cipherText);
        byte[] plainText = decrypt(rawKey,encrypted);
        return new String(plainText);
    }

    private static byte[] getRawKey(byte[] seed)throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG","Crypto");
        secureRandom.setSeed(seed);
        keyGenerator.init(128,secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] plain)throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(raw,"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,keySpec);
        return cipher.doFinal(plain);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(raw,"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,keySpec);
        return cipher.doFinal(encrypted);
    }

    private static String toHex(String txt){
        return toHex(txt.getBytes());
    }

    private static byte[] toByte(String hexString){
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i=0;i<len;i++)
            result[i] = Integer.valueOf(hexString.substring(2*i,2*i+2),16).byteValue();
        return result;
    }

    private static String toHex(byte[] buff){
        if (buff==null)
            return "";
        StringBuffer buffer = new StringBuffer(2*buff.length);
        for (int i=0;i<buff.length;i++)
            appendHex(buffer,buff[i]);
        return buffer.toString();
    }

    private static void appendHex(StringBuffer stringBuffer, byte b){
        stringBuffer.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
