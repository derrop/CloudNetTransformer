package com.github.derrop.cloudnettransformer.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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

    public static String encryptToSHA256Base64(String text) {
        return Base64.getEncoder().encodeToString(encryptToSHA256(text.getBytes(StandardCharsets.UTF_8)));
    }

    public static byte[] encryptToSHA256(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }

        return null;
    }

}
