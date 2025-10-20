package com.dhrubok.reactive_bank.security.service;

import com.dhrubok.reactive_bank.repository.UserRepository;
import com.dhrubok.reactive_bank.security.principle.UserDetailsPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(UserDetailsPrinciple::new)
                .map(userDetails -> (UserDetails) userDetails)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }
}


