package com.dhrubok.reactive_bank.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SignInRequest {
    @NotBlank(message = "Please provide you registered email.")
    @Email(message = "You email is not valid, please provide a valid email.")
    private String email;

    @NotBlank(message = "Password required.")
    private String password;
}
