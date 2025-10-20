package com.dhrubok.reactive_bank.controller;

import com.dhrubok.reactive_bank.DTO.response.BalanceResponse;
import com.dhrubok.reactive_bank.entity.Account;
import com.dhrubok.reactive_bank.model.ApiResponse;
import com.dhrubok.reactive_bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.dhrubok.reactive_bank.constant.SuccessConstant.BALANCE_CHECK_SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/check-balance")
    public Mono<ApiResponse<BalanceResponse>> checkBalance(Authentication authentication){
        String email = authentication.getName();

        return accountService
                .checkBalance(email)
                .map(BalanceResponse::new)
                .map(response->ApiResponse.success(BALANCE_CHECK_SUCCESS,response,OK));
    }

}
