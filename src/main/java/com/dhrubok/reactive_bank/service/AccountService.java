package com.dhrubok.reactive_bank.service;


import com.dhrubok.reactive_bank.DTO.request.BalanceDepositRequest;
import com.dhrubok.reactive_bank.DTO.request.BalanceTransferRequest;
import com.dhrubok.reactive_bank.entity.Account;
import com.dhrubok.reactive_bank.entity.Transaction;
import com.dhrubok.reactive_bank.exception.DuplicateUserException;
import com.dhrubok.reactive_bank.exception.InsufficientFundsException;
import com.dhrubok.reactive_bank.repository.AccountRepository;
import com.dhrubok.reactive_bank.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialNotFoundException;


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
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CredentialNotFoundException(USER_NOT_FOUND))))
                .flatMap(user -> accountRepository.findByUserId(user.getId()));
    }

    @Transactional
    public Mono<Transaction> depositBalance(String email, BalanceDepositRequest amount) {
        return userRepository
                .findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CredentialNotFoundException(USER_NOT_FOUND))))
                .flatMap(user -> accountRepository.findByUserId(user.getId())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new CredentialNotFoundException(ACCOUNT_NOT_FOUND))))
                        .flatMap(account -> {
                            account.setBalance(account.getBalance().add(amount.getAmount()));
                            return accountRepository.save(account)
                                    .flatMap(savedAccount -> transactionService.createTransaction(
                                            DEPOSIT_FROM,
                                            TRANSACTION_TYPE_DEPOSIT,
                                            account,
                                            amount.getAmount()
                                    ));
                        })
                );
    }

    public Mono<Transaction> transferBalance(
            String email,
            @Valid BalanceTransferRequest balanceTransferRequest
    ) {
        Mono<Account> senderAccount = userRepository.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(USER_NOT_FOUND))))
                .flatMap(user -> accountRepository
                        .findByUserId(user.getId())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(ACCOUNT_NOT_FOUND))))
                );

        Mono<Account> receiverAccount = accountRepository.findByAccountNumber(balanceTransferRequest.getReceiverAccountNumber())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(ACCOUNT_NOT_FOUND))));


        return Mono.zip(senderAccount, receiverAccount)
                .flatMap(tuple1 -> {

                    Account sender = tuple1.getT1();
                    Account receiver = tuple1.getT2();

                    if(sender.getAccountNumber().equals(receiver.getAccountNumber())) {
                        return Mono.error(new DuplicateUserException(SAME_SENDER_AND_RECEIVER));
                    }

                    if (sender.getBalance().compareTo(balanceTransferRequest.getAmount()) < 0) {
                        return Mono.error(new InsufficientFundsException(INSUFFICIENT_BALANCE));
                    }

                    sender.setBalance(sender.getBalance().subtract(balanceTransferRequest.getAmount()));
                    receiver.setBalance(receiver.getBalance().add(balanceTransferRequest.getAmount()));

                    return Mono.when(
                            accountRepository.save(sender),
                            accountRepository.save(receiver)
                    ).then(transactionService.createTransaction(
                            sender,
                            TRANSACTION_TYPE_TRANSFER,
                            receiver,
                            balanceTransferRequest.getAmount()
                    ));

                });

    }
}
