package com.dhrubok.reactive_bank.service;

import com.dhrubok.reactive_bank.entity.Account;
import com.dhrubok.reactive_bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.dhrubok.reactive_bank.constant.AccountConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountNumberGeneratorService {

    private final AccountRepository accountRepository;

    public Mono<String> generateAccountNumber() {
        return accountRepository.findTopByOrderByIdDesc()
                .flatMap(existingAccount -> {
                    String lastNumber = existingAccount.getAccountNumber();

                    if (lastNumber == null || !lastNumber.startsWith(ACCOUNT_CODE)) {
                        log.info("Generated new account number: {}", INITIAL_ACCOUNT_NUMBER);
                        return Mono.just(INITIAL_ACCOUNT_NUMBER);
                    }

                    int lastNum = Integer.parseInt(lastNumber.substring(3));
                    String next = ACCOUNT_CODE + (lastNum + 1);
                    log.info("Generated new account number: {}", next);
                    return Mono.just(next);
                })
                .switchIfEmpty(Mono.just(INITIAL_ACCOUNT_NUMBER));
    }
}
