package com.dhrubok.reactive_bank.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("transactions")
@Getter
@Setter
public class Transaction {
    @Id
    private Long id;
    private Long userId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String type;
    private LocalDate date;
    private String transactionNumber;
}
