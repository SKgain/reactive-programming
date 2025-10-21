package com.dhrubok.reactive_bank.controller;

import com.dhrubok.reactive_bank.DTO.request.BalanceDepositRequest;
import com.dhrubok.reactive_bank.DTO.request.BalanceTransferRequest;
import com.dhrubok.reactive_bank.DTO.response.BalanceResponse;
import com.dhrubok.reactive_bank.DTO.response.TransactionResponse;
import com.dhrubok.reactive_bank.model.ApiResponse;
import com.dhrubok.reactive_bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.dhrubok.reactive_bank.constant.SuccessConstant.*;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/check-balance")
    @PreAuthorize("hasRole('USER')")
    public Mono<ApiResponse<BalanceResponse>> checkBalance() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .doOnNext(name -> log.info("Authenticated user: {}", name))
                .flatMap(accountService::checkBalance)
                .doOnNext(account -> log.info("Fetched account: {}", account))
                .map(account -> ApiResponse.success(
                        BALANCE_CHECK_SUCCESS,
                        new BalanceResponse(account),
                        OK
                ))
                .doOnError(err -> log.error("Error in checkBalance chain", err));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/deposit")
    public Mono<ApiResponse<TransactionResponse>> depositBalance(
            @Valid @RequestBody BalanceDepositRequest amount
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .flatMap(email -> accountService.depositBalance(email, amount))
                .map(transaction -> ApiResponse.success(
                        BALANCE_DEPOSIT_SUCCESS,
                        new TransactionResponse(transaction),
                        OK
                ));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public Mono<ApiResponse<TransactionResponse>> transferBalance(
            @Valid @RequestBody BalanceTransferRequest balanceTransferRequest
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .flatMap(email -> accountService.transferBalance(email, balanceTransferRequest))
                .map(transaction -> ApiResponse.success(
                        BALANCE_TRANSFER_SUCCESS,
                        new TransactionResponse(transaction),
                        OK
                ));
    }

}
