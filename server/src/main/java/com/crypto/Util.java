package com.crypto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Util {

    public static final Charset UTF8 = StandardCharsets.UTF_8;

    public static byte[] getRandom(byte[] bytes) {
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(bytes);
        return bytes;
    }
}
