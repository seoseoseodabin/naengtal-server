package com.example.naengtal.global.auth.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN"),
    WRONG_ID_OR_PASSWORD(HttpStatus.BAD_REQUEST, "WRONG_ID_OR_PASSWORD");

    private final HttpStatus httpStatus;
    private final String message;
}
