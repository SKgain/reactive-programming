package com.dhrubok.reactive_bank.DTO.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
public class BalanceTransferRequest {
    private String receiverAccountNumber;
    private BigDecimal amount;
}
