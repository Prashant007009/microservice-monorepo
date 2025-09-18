package com.ecommerce.auth.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    private final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60;   // 15 minutes
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 24 hours

    public String generateAccessToken(String email, long phoneNumber) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("phoneNumber", phoneNumber)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ACCESS_TOKEN_VALIDITY)))
                .signWith(key)  // ✅ specify alg + key
                .compact();
    }

    public String generateRefreshToken(String email, long phoneNumber) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("phoneNumber", phoneNumber)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(REFRESH_TOKEN_VALIDITY)))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)   // ✅ verify with alg + key
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}