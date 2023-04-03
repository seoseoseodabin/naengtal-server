package com.example.naengtal.global.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {

    private String code;
    private String message;
}