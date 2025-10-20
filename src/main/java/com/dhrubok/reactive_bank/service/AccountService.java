package com.dhrubok.reactive_bank.service;


import com.dhrubok.reactive_bank.entity.Account;
import com.dhrubok.reactive_bank.repository.AccountRepository;
import com.dhrubok.reactive_bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialNotFoundException;


import static com.dhrubok.reactive_bank.constant.ErrorConstant.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberGeneratorService accountNumberGeneratorService;

    public Mono<Account> createAccountForUser(Long userId) {
        return accountNumberGeneratorService.generateAccountNumber()
                .flatMap(accountNumber -> {
                    Account account = new Account();
                    account.setUserId(userId);
                    account.setAccountNumber(accountNumber);
                    return accountRepository.save(account);
                });
    }

    public Mono<Account> checkBalance(String email) {
        return userRepository
                .findByEmail(email)
                .flatMap(user-> accountRepository.findByUserId(user.getId()))
                .switchIfEmpty(Mono.defer(()->Mono.error(new CredentialNotFoundException(ACCOUNT_NOT_FOUND))));
    }
}
