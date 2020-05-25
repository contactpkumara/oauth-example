package com.bola.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface EncryptDecryptService {
    String generateEncryptPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException;

    boolean validatePassword(String userPassword, String dbPassword) throws NoSuchAlgorithmException, InvalidKeySpecException;

    String generateRandomPassword(int length);
}
