package com.dhrubok.reactive_bank.DTO.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SignInResponse {
    private String accessToken;

    public SignInResponse(String accessToken){
        this.accessToken = accessToken;
    }
}
