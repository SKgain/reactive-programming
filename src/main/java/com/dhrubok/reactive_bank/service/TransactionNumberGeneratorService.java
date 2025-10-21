package com.dhrubok.reactive_bank.service;

import com.dhrubok.reactive_bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.dhrubok.reactive_bank.constant.AccountConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionNumberGeneratorService {
    private final TransactionRepository transactionRepository;

    public Mono<String> generateTransactionNumber() {

        return transactionRepository.findTopByOrderByIdDesc()
                .flatMap(transaction -> {

                    String lastTransactionNumber = transaction.getTransactionNumber();

                    if (lastTransactionNumber == null || !lastTransactionNumber.startsWith(TRANSACTION_CODE)) {
                        log.info("Generated new account number: {}", INITIAL_TRANSACTION_NUMBER);
                        return Mono.just(INITIAL_TRANSACTION_NUMBER);
                    }

                        int lastNum = Integer.parseInt(lastTransactionNumber.substring(3));
                        String next = TRANSACTION_CODE + (lastNum + 1);
                        log.info("Generated new account number: {}", next);
                        return Mono.just(next);

                })
                .switchIfEmpty(Mono.defer(()-> Mono.just(INITIAL_TRANSACTION_NUMBER)));
    }
}
