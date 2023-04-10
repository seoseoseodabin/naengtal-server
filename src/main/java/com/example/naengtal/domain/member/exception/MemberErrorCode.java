package com.example.naengtal.domain.member.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    UNAVAILABLE_ID(HttpStatus.BAD_REQUEST, "Unavailable id"),
    WRONG_CONFIRM_PASSWORD(HttpStatus.BAD_REQUEST, "Wrong confirm password"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "member not found"),
    ;

    private HttpStatus httpStatus;
    private String message;
}
