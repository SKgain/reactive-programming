package com.dhrubok.reactive_bank.constant;

import java.util.Base64;

public final class SecurityConstant {
    public static final String[] PUBLIC_URIS = {
            "/api/auth/sign-in",
            "/api/auth/sign-up",
            "/public/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };
    public static final String JWT_SECRET = Base64.getEncoder().encodeToString(
            "MY_SUPER_SECRET_KEY_12345678901234567890".getBytes()
    );
    public static final String JWT_ALGORITHM = "HmacSHA256";
    public static final long JWT_EXPIRATION_MILLIS = 1000 * 60 * 60 * 10; // 10 hours
}
