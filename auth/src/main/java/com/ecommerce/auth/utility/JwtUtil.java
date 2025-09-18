package com.ecommerce.auth.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60;   // 1 hour
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 24 hours

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String email, long phoneNumber) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("phoneNumber", phoneNumber)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ACCESS_TOKEN_VALIDITY)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String email, long phoneNumber) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("phoneNumber", phoneNumber)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(REFRESH_TOKEN_VALIDITY)))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}