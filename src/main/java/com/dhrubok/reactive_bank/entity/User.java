package com.dhrubok.reactive_bank.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Getter
@Setter
public class User {
    @Id
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String role;
}
