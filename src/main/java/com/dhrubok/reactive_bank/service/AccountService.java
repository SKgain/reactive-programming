package com.dhrubok.reactive_bank.service;


import com.dhrubok.reactive_bank.DTO.request.BalanceDepositRequest;
import com.dhrubok.reactive_bank.constant.AccountConstant;
import com.dhrubok.reactive_bank.entity.Account;
import com.dhrubok.reactive_bank.repository.AccountRepository;
import com.dhrubok.reactive_bank.repository.TransactionRepository;
import com.dhrubok.reactive_bank.repository.UserRepository;
import io.netty.util.AsyncMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialNotFoundException;


import java.math.BigDecimal;

import static com.dhrubok.reactive_bank.constant.AccountConstant.*;
import static com.dhrubok.reactive_bank.constant.ErrorConstant.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberGeneratorService accountNumberGeneratorService;
    private final TransactionService transactionService;


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
                .flatMap(user -> accountRepository.findByUserId(user.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CredentialNotFoundException(ACCOUNT_NOT_FOUND))));
    }

    @Transactional
    public Mono<Account> depositBalance(String email, BalanceDepositRequest amount) {
        return userRepository
                .findByEmail(email)
                .flatMap(user -> {
                            accountRepository.findByUserId(user.getId())
                                    .flatMap(account -> {
                                                account.setBalance(account.getBalance().add(amount.getAmount()));
                                                return accountRepository.save(account);
                                            }
                                    );
                            transactionService.createTransaction(user,account,TRANSACTION_TYPE_DEPOSIT,)

                        }
                )
                .switchIfEmpty(
                        Mono.defer(() -> Mono.error(new CredentialNotFoundException(ACCOUNT_NOT_FOUND)))
                );

    }
}
