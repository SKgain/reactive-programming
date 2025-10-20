package com.dhrubok.reactive_bank.security.jwt.filter;

import com.dhrubok.reactive_bank.security.jwt.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter implements WebFilter {

    private final JWTService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token → continue without authentication
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        return jwtService.extractUserName(token)
                .flatMap(username ->
                        userDetailsService.findByUsername(username)
                                .flatMap(userDetails ->
                                        jwtService.validateToken(token, userDetails)
                                                .flatMap(isValid -> {
                                                    if (Boolean.TRUE.equals(isValid)) {
                                                        log.info("User object class: {}", userDetails.getClass().getName());
                                                        log.info("Calling isAccountNonExpired()...");
                                                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                                                userDetails, null, userDetails.getAuthorities());
                                                        return chain.filter(exchange)
                                                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                                                    }
                                                    // Invalid token → continue without authentication
                                                    return chain.filter(exchange);
                                                })
                                )
                )
                // If username not found → continue without authentication
                .switchIfEmpty(chain.filter(exchange))
                .onErrorResume(e -> {
                    log.warn("JWT processing failed: {}", e.getMessage());
                    return chain.filter(exchange);  // just continue chain without authentication
                });
    }


}
