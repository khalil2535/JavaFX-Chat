package com.model;


import com.model.crypto.AES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        String message = "Testt";
        System.out.println(message);

        String key = AES.generateKey();

        String encrypted = AES.encrypt(message, key);
        System.out.println(encrypted);

        String plain = AES.decrypt(encrypted, key);
        System.out.println(plain);
    }
}
