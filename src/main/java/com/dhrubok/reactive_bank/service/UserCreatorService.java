package com.dhrubok.reactive_bank.service;

import com.dhrubok.reactive_bank.DTO.request.SignUpRequest;
import com.dhrubok.reactive_bank.entity.User;
import com.dhrubok.reactive_bank.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.dhrubok.reactive_bank.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class UserCreatorService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> createUser(@Valid SignUpRequest signUpRequest){
        User newUser = new User();
        newUser.setFullName(signUpRequest.getFullName());
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        newUser.setRole(INITIAL_USER);

        return userRepository.save(newUser);
    }
}
