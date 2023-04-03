package com.example.naengtal.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenDto {
    private String accessToken;
    private long accessTokenValidateTime;
}
