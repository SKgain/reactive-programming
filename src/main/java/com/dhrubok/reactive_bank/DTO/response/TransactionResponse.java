package com.dhrubok.reactive_bank.DTO.response;

import com.dhrubok.reactive_bank.entity.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class TransactionResponse {
    private String senderAccountNumber;
    private String recipientAccountNumber;
    private BigDecimal amount;
    private LocalDate date;

    public TransactionResponse(Transaction transaction) {

        this.senderAccountNumber = transaction.getSenderAccountNumber();
        this.recipientAccountNumber = transaction.getReceiverAccountNumber();
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
    }
}
