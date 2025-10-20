package com.dhrubok.reactive_bank.controller;

import com.dhrubok.reactive_bank.DTO.request.SignInRequest;
import com.dhrubok.reactive_bank.DTO.request.SignUpRequest;
import com.dhrubok.reactive_bank.DTO.response.SignUpResponse;
import com.dhrubok.reactive_bank.model.ApiResponse;
import com.dhrubok.reactive_bank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.dhrubok.reactive_bank.constant.SuccessConstant.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
@Slf4j
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public Mono<ApiResponse<SignUpResponse>> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest
    ) {

        return userService
                .signUp(signUpRequest)
                .map(userResponse->ApiResponse.success(SIGN_UP_SUCCESS,userResponse,OK));
    }

    @PostMapping("/sign-in")
    public Mono<?> signIn(
            @Valid @RequestBody SignInRequest signInRequest
    ){
        return userService
                .signIn(signInRequest)
                .map(userResponse-> ApiResponse.success(LOG_IN_SUCCESS,userResponse,OK));
    }
}
