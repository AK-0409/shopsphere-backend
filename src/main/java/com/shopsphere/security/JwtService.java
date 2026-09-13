package com.shopsphere.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;

	private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(String username) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + ACCESS_TOKEN_EXPIRATION
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    
    public String generateRefreshToken(String username) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + REFRESH_TOKEN_EXPIRATION
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .claim("tokenType", "refresh")
                .signWith(secretKey)
                .compact();
    }
    
    public boolean isRefreshToken(String token) {

        try {
            String tokenType = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("tokenType", String.class);

            return "refresh".equals(tokenType);

        } catch (Exception e) {
            return false;
        }
    }
    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}