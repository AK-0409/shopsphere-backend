package com.shopsphere.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "shopsphere-secret-key-must-be-at-least-32-characters-long";

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60; 

    private final SecretKey secretKey;

    public JwtService() {
        this.secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + EXPIRATION_TIME
        );

        return Jwts.builder().subject(username).issuedAt(now).expiration(expiration).signWith(secretKey).compact();
    }
    
    public String extractUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }
    
    public boolean isTokenValid(String token) {

        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}