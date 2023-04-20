package com.example.naengtal.domain.alarm.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AlarmErrorCode implements ErrorCode {

    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "alarm not found"),
    NOT_OWN_ALARM(HttpStatus.BAD_REQUEST, "not own alarm"),
    ;

    private HttpStatus httpStatus;
    private String message;
}
