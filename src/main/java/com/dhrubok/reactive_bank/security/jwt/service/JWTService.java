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

    // Generate JWT token
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

    // Extract username
    public Mono<String> extractUserName(String token) {
        if (token == null || token.isBlank()) return Mono.empty();
        return parseToken(token)
                .map(Claims::getSubject)
                .onErrorResume(e -> {
                    log.warn("Failed to extract username from token: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    // Validate token
    public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
        return parseToken(token)
                .map(claims -> claims.getSubject().equals(userDetails.getUsername())
                        && claims.getExpiration().after(new Date()))
                .onErrorReturn(false);
    }

    // Extract specific claim
    public <T> Mono<T> extractClaim(String token, Function<Claims, T> claimsResolver) {
        return parseToken(token)
                .map(claimsResolver)
                .onErrorResume(e -> {
                    log.error("Failed to extract claim: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    // Reactive token parsing
    private Mono<Claims> parseToken(String token) {
        return Mono.fromCallable(() ->
                Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
