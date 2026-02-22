package com.myretro.security;

import java.util.Date;

import javax.crypto.SecretKey;

import com.myretro.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes());
        this.accessTokenExpiration = properties.accessTokenExpiration();
        this.refreshTokenExpiration = properties.refreshTokenExpiration();
    }

    public String generateAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
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

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }
}
