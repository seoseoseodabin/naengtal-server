package com.example.naengtal.global.auth.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "invalid token"),
    WRONG_ID_OR_PASSWORD(HttpStatus.BAD_REQUEST, "wrong id or password");

    private final HttpStatus httpStatus;
    private final String message;
}
