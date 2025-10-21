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
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionNumberGeneratorService transactionNumberGeneratorService;

    public Mono<Transaction> createTransaction(
            Account senderAccount,
            String transactionType,
            Account receiverAccount,
            BigDecimal amount
    ) {
        return transactionNumberGeneratorService.generateTransactionNumber()
                .flatMap(tnxnum-> {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionNumber(tnxnum);
                    transaction.setDate(LocalDate.now());
                    transaction.setAmount(amount);
                    transaction.setType(transactionType);
                    transaction.setSenderAccountNumber(senderAccount.getAccountNumber());
                    transaction.setReceiverAccountNumber(receiverAccount.getAccountNumber());
                    transaction.setUserId(receiverAccount.getUserId());
                    return transactionRepository.save(transaction);
                });
    }
}
