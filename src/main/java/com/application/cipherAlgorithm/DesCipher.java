package com.application.cipherAlgorithm;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DesCipher {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

    }
    private SecretKey secretKey;

    public String generateKey() throws NoSuchAlgorithmException
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        secretKey = keyGen.generateKey();
        String key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        return key;
    }


    public SecretKey decodeKey(String key){
        byte[] decodedKey = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
        return secretKey;
    }

    public String encrypt (String inputText) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException
    {
        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] inputTextByte = inputText.getBytes();
        byte[] encryptedTextByte = desCipher.doFinal(inputTextByte);

        String encryptedText = Base64.getEncoder().encodeToString(encryptedTextByte);

        return encryptedText;
    }

    public String decrypt (String inputText, String key) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException
    {
        byte[] inputTextByte = Base64.getDecoder().decode(inputText);

        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, decodeKey(key));
        byte[] decryptedTextByte = desCipher.doFinal(inputTextByte);
        String decryptedText = new String(decryptedTextByte);
        return decryptedText;
    }
}
