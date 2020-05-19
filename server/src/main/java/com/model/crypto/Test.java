package com.model.crypto;


public class Test {
    public static void main(String[] args) throws Exception {
        String origin = "Hello World";
        String key = AES.generateKey();
        System.out.println("origin: " + origin);

        String encrypted = AES.encrypt(origin, key);
        System.out.println("encrypted: " + encrypted);

        String decrypted = AES.decrypt(encrypted, key);
        System.out.println("decrypted: " + decrypted);

    }
}
