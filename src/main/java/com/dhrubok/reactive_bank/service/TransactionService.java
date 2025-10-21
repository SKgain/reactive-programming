package com.dhrubok.reactive_bank.service;

import com.dhrubok.reactive_bank.entity.Account;
import com.dhrubok.reactive_bank.entity.Transaction;
import com.dhrubok.reactive_bank.entity.User;
import com.dhrubok.reactive_bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionNumberGeneratorService transactionNumberGeneratorService;

    public Mono<Transaction> createTransaction(
            User sender,
            Account senderAccount,
            String transactionType,
            Account receiverAccount,
            BigDecimal amount
            ) {

    }
}
