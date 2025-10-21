package com.dhrubok.reactive_bank.repository;

import com.dhrubok.reactive_bank.entity.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction,Long> {
    Mono<Transaction> findTopByOrderByIdDesc();
}
