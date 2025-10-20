package com.dhrubok.reactive_bank.security.jwt.service;

import com.dhrubok.reactive_bank.constant.SecurityConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JWTService {

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(SecurityConstant.JWT_SECRET)
    );

    // 🔹 Generate JWT Token reactively
    public Mono<String> generateToken(String email) {
        return Mono.fromSupplier(() -> {
            Map<String, Object> claims = new HashMap<>();
            return Jwts.builder()
                    .claims(claims)
                    .subject(email.toLowerCase())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + SecurityConstant.JWT_EXPIRATION_MILLIS))
                    .signWith(secretKey)
                    .compact();
        });
    }

    // 🔹 Extract username reactively
    public Mono<String> extractUserName(String token) {
        return Mono.fromCallable(() -> {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token is missing");
            }
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }).onErrorResume(e -> {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return Mono.empty();
        });
    }

    // 🔹 Validate token reactively
    public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
        return extractUserName(token)
                .map(username -> username.equals(userDetails.getUsername()))
                .defaultIfEmpty(false)
                .flatMap(valid -> isTokenExpired(token).map(expired -> valid && !expired));
    }

    // 🔹 Check expiration reactively
    private Mono<Boolean> isTokenExpired(String token) {
        return Mono.fromCallable(() -> {
            Date expiration = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        }).onErrorReturn(true); // treat invalid tokens as expired
    }

    // 🔹 Extract specific claim reactively
    public <T> Mono<T> extractClaim(String token, Function<Claims, T> claimsResolver) {
        return extractAllClaims(token)
                .map(claimsResolver)
                .onErrorResume(e -> {
                    log.error("Failed to extract claim: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    // 🔹 Get all claims reactively
    private Mono<Claims> extractAllClaims(String token) {
        return Mono.fromCallable(() -> {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token is empty");
            }
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        });
    }
}
