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
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import javax.security.auth.login.CredentialException;
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
        if (token == null || token.isBlank()) {
            return Mono.empty(); // no token
        }
        return Mono.fromCallable(() ->
                        Jwts.parser()
                                .verifyWith(secretKey)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .getSubject()
                ).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    log.warn("Failed to extract username from token: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    // 🔹 Validate token reactively
    public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
        return Mono.fromCallable(() -> {
                    String username = Jwts.parser()
                            .verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getSubject();
                    return username.equals(userDetails.getUsername());
                }).subscribeOn(Schedulers.boundedElastic())
                .onErrorReturn(false);
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
