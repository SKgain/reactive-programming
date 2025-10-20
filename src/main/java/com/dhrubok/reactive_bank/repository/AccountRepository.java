package com.dhrubok.reactive_bank.repository;

import com.dhrubok.reactive_bank.entity.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account,Long> {
    Mono<Account> findTopByOrderByIdDesc();
}
