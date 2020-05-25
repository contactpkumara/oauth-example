package com.bola.services.impl;

import com.bola.services.EncryptDecryptService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EncryptDecryptServiceImpl implements EncryptDecryptService {
    private String securityKey = "PBKDF2WithHmacSHA1";
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "@$!%*#?&";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + SPECIAL + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();

    @Override
    public String generateEncryptPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = this.getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(this.securityKey);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + this.byteToHex(salt) + ":" + this.byteToHex(hash);
    }

    @Override
    public boolean validatePassword(String userPassword, String dbPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = dbPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = this.hexToByte(parts[1]);
        byte[] hash = this.hexToByte(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(userPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    @Override
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException();
        } else {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                // 0-62 (exclusive), random returns 0-61
                int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
                char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
                sb.append(rndChar);
            }
            Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
            Matcher matcher = pattern.matcher(sb.toString());
            if (matcher.matches()) {
                return sb.toString();
            } else {
                return generateRandomPassword(length);
            }
        }
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private String byteToHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0".concat(String.valueOf(paddingLength)).concat("d"), 0) + hex;
        } else {
            return hex;
        }
    }

    private byte[] hexToByte(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
