package org.example.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    // 构造器注入，避免空指针
    @Autowired
    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // 强制UTF-8编码，生成合规密钥
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = keyBytes.length >= 32 ? Keys.hmacShaKeyFor(keyBytes) : Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(String username, int role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey) // 仅传SecretKey，让JJWT自动匹配算法
                .compact();
    }

    // 从令牌中获取用户名
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}