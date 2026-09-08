package com.aryan.razorpay.merchant_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String email, UUID merchantId, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("merchant_id", merchantId)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(60*100)))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims verifyToken(String token) {
        // this may lead to exception if our token is already expired
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public String extractMerchantId(Claims claims) {
        return claims.get("merchant_id", String.class);
    }
}
