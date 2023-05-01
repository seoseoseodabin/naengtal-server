package com.example.naengtal.global.auth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignInRequestDto {

    @NotBlank
    private String id;

    @NotBlank
    private String password;

    @NotBlank
    private String fcmToken;
}
