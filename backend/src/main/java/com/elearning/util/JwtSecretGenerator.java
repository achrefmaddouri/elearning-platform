package com.elearning.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        // Generate a secure key for HS512
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        
        System.out.println("Generated secure JWT secret key:");
        System.out.println(base64Key);
        System.out.println("Key length: " + keyBytes.length * 8 + " bits");
    }
}
