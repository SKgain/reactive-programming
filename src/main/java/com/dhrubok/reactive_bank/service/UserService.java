package com.dhrubok.reactive_bank.service;

import com.dhrubok.reactive_bank.DTO.request.SignInRequest;
import com.dhrubok.reactive_bank.DTO.request.SignUpRequest;
import com.dhrubok.reactive_bank.DTO.response.SignInResponse;
import com.dhrubok.reactive_bank.DTO.response.SignUpResponse;
import com.dhrubok.reactive_bank.entity.User;
import com.dhrubok.reactive_bank.exception.DuplicateUserException;
import com.dhrubok.reactive_bank.repository.UserRepository;
import com.dhrubok.reactive_bank.security.jwt.service.JWTService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialException;
import javax.security.auth.login.CredentialNotFoundException;
import java.util.Map;

import static com.dhrubok.reactive_bank.constant.AppConstant.*;
import static com.dhrubok.reactive_bank.constant.ErrorConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final AccountService accountService;
    private final UserCreatorService userCreatorService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JWTService jWTService;

    public Mono<SignUpResponse> signUp(@Valid SignUpRequest signUpRequest) {
        return userRepository
                .findByEmail(signUpRequest.getEmail())
                .flatMap(existUser -> Mono.<SignUpResponse>error(new DuplicateUserException(USER_ALREADY_EXIST)))
                .switchIfEmpty(
                        Mono.defer(() -> {
                            User newUser = new User();
                            newUser.setFullName(signUpRequest.getFullName());
                            newUser.setEmail(signUpRequest.getEmail().toLowerCase());
                            newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
                            newUser.setRole(INITIAL_USER);

                            return userRepository.save(newUser)
                                    .flatMap(savedUser -> accountService.createAccountForUser(savedUser.getId())
                                            .map(savedAccount -> {
                                                SignUpResponse userResponse = modelMapper.map(savedUser, SignUpResponse.class);
                                                userResponse.setAccountNumber(savedAccount.getAccountNumber());
                                                return userResponse;
                                            })
                                    );
                        })
                );
    }

    public Mono<?> signIn(@Valid SignInRequest signInRequest) {
        return authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                        signInRequest.getEmail().toLowerCase(),
                        signInRequest.getPassword()))
                .flatMap(authentication->{
                    if(authentication.isAuthenticated()) {
                        return jWTService
                                .generateToken(signInRequest.getEmail().toLowerCase())
                                .map(SignInResponse::new);
                    }
                    else {
                        return Mono.error(new CredentialException("Invalid email or password"));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CredentialNotFoundException("Invalid email or password"))));
    }
}
