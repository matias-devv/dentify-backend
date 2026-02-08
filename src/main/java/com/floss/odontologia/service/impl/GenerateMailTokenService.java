package com.floss.odontologia.service.impl;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class generateMailTokenService {

    /**
     * Generate a unique token to confirm attendance
     */
    public class GenerateMailTokenService {

        private static final SecureRandom RANDOM = new SecureRandom();

        public String generateConfirmationToken() {
            byte[] randomBytes = new byte[24]; // 192 bits
            RANDOM.nextBytes(randomBytes);

            return Base64.getUrlEncoder()
                         .withoutPadding()
                         .encodeToString(randomBytes);
        }
    }
}
