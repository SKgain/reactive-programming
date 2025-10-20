package com.dhrubok.reactive_bank.security.service;


import com.dhrubok.reactive_bank.repository.UserRepository;
import com.dhrubok.reactive_bank.security.principle.UserDetailsPrinciple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.dhrubok.reactive_bank.constant.ErrorConstant.ACCOUNT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("findByUsername: {}", username);
        return userRepository.findByEmail(username)
                .map(UserDetailsPrinciple::new)
                .map(userDetails -> (UserDetails) userDetails)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(ACCOUNT_NOT_FOUND)));
    }
}


