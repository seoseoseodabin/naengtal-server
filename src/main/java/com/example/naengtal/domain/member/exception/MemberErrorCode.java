package com.example.naengtal.domain.member.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    UNAVAILABLE_ID(HttpStatus.BAD_REQUEST, "unavailable id"),
    WRONG_CONFIRM_PASSWORD(HttpStatus.BAD_REQUEST, "wrong confirm password"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "member not found"),
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "cannot invite self"),
    ;

    private HttpStatus httpStatus;
    private String message;
}
