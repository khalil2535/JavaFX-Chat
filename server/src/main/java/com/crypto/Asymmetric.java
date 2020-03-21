package com.crypto;

public class Asymmetric {
    // TODO use RSA Cipher,KeyPair,KeyPairGenerator classes
    // TODO use SHA3-512  from org.bouncycastle.jcajce.provider.digest.SHA3

    /**
     * TODO
     *
     * @param msg the message to encrypt
     * @param key the key of the encryption
     * @return the encryptedMsg
     */
    public static String encrypt(String msg, String key) {
        String encryptedMsg = msg;
        throw new UnsupportedOperationException("this method isn't implemented yet");
        //return encryptedMsg;
    }

    /**
     * TODO
     *
     * @param encryptedMsg the encrypted message to decrypt
     * @param key          the key of decryption
     * @return the origin msg
     */
    public static String decrypt(String encryptedMsg, String key) {
        String msg = encryptedMsg;
        throw new UnsupportedOperationException("this method isn't implemented yet");
        //return msg;
    }

}
