package com.dhrubok.reactive_bank.DTO.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignUpResponse {
    private String email;
    private String fullName;
    private String accountNumber;
}
