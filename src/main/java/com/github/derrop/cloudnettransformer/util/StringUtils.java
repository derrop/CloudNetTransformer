package com.github.derrop.cloudnettransformer.util;

import java.util.Random;

public class StringUtils {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    private StringUtils() {
        throw new UnsupportedOperationException();
    }

    public static String randomString(int length) {
        Random random = new Random();
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            chars[i] = ALPHABET[random.nextInt(ALPHABET.length)];
        }

        return String.valueOf(chars);
    }

}
