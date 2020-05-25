package com.bola.util;

import com.bola.services.EncryptDecryptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * <h1>Custom Password Encryptor</h1>
 * BCrypt Hash Password Method used for encrypt the password
 * Check encrypted password equality
 *
 * @author Prasad Kumara
 * @version 1.0.0
 * @since 2020-05-21
 */
public class CustomPasswordEncrypt implements PasswordEncoder {

    private static final Logger log = LoggerFactory.getLogger(CustomPasswordEncrypt.class);
    @Autowired
    EncryptDecryptService encryptDecryptService;

    /**
     * Encrypt given password char sequence array
     *
     * @param charSequence char sequence array
     * @return Encrypt password hashed string
     */
    @Override
    public String encode(CharSequence charSequence) {
        String hashed = null;
        try {
            hashed = encryptDecryptService.generateEncryptPassword(charSequence.toString());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e.getMessage());
        }
        return hashed;
    }

    /**
     * Check Given two encrypt password string
     *
     * @param charSequence    char sequence array
     * @param encodedPassword encrypt password string
     * @return boolean value
     */
    @Override
    public boolean matches(CharSequence charSequence, String encodedPassword) {
        try {
            return encryptDecryptService.validatePassword(charSequence.toString(), encryptDecryptService.generateEncryptPassword(encodedPassword));
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
