package com.example.manger.util;

import com.example.manger.common.ApiResponse;
import com.example.manger.context.BaseContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author：李子政
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.rememberExpiration}")
    private Long rememberExpiration;

    private static final String SESSION_PREFIX = "user:session:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 对外提供读取配置的简便方法（用于 Controller）
    public long getExpiration() {
        return expiration;
    }

    public long getRememberExpiration() {
        return rememberExpiration;
    }
    private SecretKey getSigningKey() {
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 新增：支持自定义过期时间
    public String generateToken(Long userId, long ttlSeconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, ttlSeconds);
    }

    // 新增：支持自定义过期时间
    public String createToken(Map<String, Object> claims, long ttlSeconds) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ttlSeconds * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean checkToken(String token) {
        log.info("checkToken token: {}", token);
        //检验token本身
        if (!validateToken(token)) {
            return false;
        }
        Map<String, Object> session = getSessionFromRedis(token);
        if (session == null) {
            return false;
        }
        BaseContext.setCurrentId(Long.valueOf((Integer) session.get("id")));
        return true;
    }


    /**
     * 从 Redis 获取会话，并根据 JWT 的 exp 续期
     */
    private Map<String, Object> getSessionFromRedis(String token) {
        try {
            String key = SESSION_PREFIX + token;
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) return null;

            // 根据 JWT 的 exp 计算剩余有效期，进行滑动续期
            long newTtl = computeRemainingTtlByJwt(token);
            if (newTtl > 0) {
                redisTemplate.expire(key, newTtl, TimeUnit.SECONDS);
            }

            return new ObjectMapper().readValue(json,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Redis读取会话失败", e);
            throw new RuntimeException("Redis读取失败", e);
        }
    }

    /**
     * 存储会话到 Redis（自定义 TTL）
     */
    public void storeSessionInRedis(String token, Map<String, Object> userInfo, long ttlSeconds) {
        log.info("storeSessionInRedis token: {}", token);
        try {
            String key = SESSION_PREFIX + token;
            String value = new ObjectMapper().writeValueAsString(userInfo);
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis存储会话失败", e);
            throw new RuntimeException("Redis存储失败", e);
        }
    }

    public void inValidate(String token) {
        log.info("inValidate token: {}", token);
        try {
            String key = SESSION_PREFIX + token;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis删除会话失败", e);
            throw new RuntimeException("Redis删除失败", e);
        }
    }

    /**
     * 基于 JWT exp 计算当前应续期的剩余 TTL（秒）
     */
    private long computeRemainingTtlByJwt(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expirationDate = claims.getExpiration();
            long remainingMs = expirationDate.getTime() - System.currentTimeMillis();
            return Math.max(0, remainingMs / 1000);
        } catch (Exception e) {
            log.error("JWT解析过期时间失败，token: {}", token, e);
            return 0;
        }
    }

    /**
     * 校验 Token 本身是否有效（签名、格式、过期）
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token 无效，token: {}", token, e);
            return false;
        }
    }

    /**
     * 从 Token 中解析用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("解析用户ID失败，token: {}", token, e);
            return null;
        }
    }
}