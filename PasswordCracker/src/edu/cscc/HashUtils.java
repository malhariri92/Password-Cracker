package edu.cscc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    /**
     * Convert password to hexadecimal encoded SHA-256 hash
     * @param password plaintext password
     * @return hashed password as a hexadecimal String
     */
    public static String hashPassword(String password) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-256 algorithm is not available!");
            e.printStackTrace();
            System.exit(-1);
        }
        return encodeHexString(hash);
    }

    /**
     * Encode a byte array as a hexadecimal String
     * @param byteArray byte array to be encoded
     * @return String version of byte array
     */
    private static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    /**
     * Convert a byte to hexadecimal
     * @param num byte to convert
     * @return UPPER CASE hexadecimal String
     */
    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits).toUpperCase();
    }

}
