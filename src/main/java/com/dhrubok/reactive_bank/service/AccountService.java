package com.dhrubok.reactive_bank.service;

import com.dhrubok.reactive_bank.entity.Account;
import com.dhrubok.reactive_bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
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
}
