package com.dhrubok.reactive_bank.DTO.response;

import com.dhrubok.reactive_bank.entity.Account;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
public class BalanceResponse {
    private String accountNumber;
    private BigDecimal balance;

    public BalanceResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
    }
}
