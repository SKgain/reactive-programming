package com.dhrubok.reactive_bank.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("accounts")
@Setter
@Getter
public class Account {

    @Id
    private Long id;
    private Long userId;
    private String accountNumber;
    private BigDecimal balance;

}
