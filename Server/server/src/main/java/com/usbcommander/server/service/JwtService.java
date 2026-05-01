package com.usbcommander.server.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService implements IJwtService{

    private final SecretKey key;
    private final long accessTokenSeconds;
    private final long refreshTokenSeconds;

    public JwtService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-seconds}") long accessTokenSeconds,
            @Value("${jwt.refresh-token-expiration-seconds}") long refreshTokenSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenSeconds = accessTokenSeconds;
        this.refreshTokenSeconds = refreshTokenSeconds;
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenSeconds * 1000L);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole() != null ? user.getRole().getName() : null)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenSeconds * 1000L);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setId(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getJtiFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims == null ? null : claims.getId();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims == null ? null : claims.getSubject();
    }

    public boolean validateToken(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) return false;
        Date today = new Date();
        return claims != null && today.before(claims.getExpiration()) && today.after(claims.getIssuedAt());
    }

    public boolean isRefreshToken(String token) {
        Claims c = parseClaims(token);
        if (c == null) return false;
        Object t = c.get("type");
        return t != null && "refresh".equals(t.toString());
    }

    public long getAccessTokenSeconds() {
        return accessTokenSeconds;
    }

    public long getRefreshTokenSeconds() {
        return refreshTokenSeconds;
    }
}
