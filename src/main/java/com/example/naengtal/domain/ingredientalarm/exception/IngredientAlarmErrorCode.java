package com.example.naengtal.domain.ingredientalarm.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum IngredientAlarmErrorCode implements ErrorCode {

    INGREDIENT_ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "ingredient alarm not found");

    private final HttpStatus httpStatus;
    private final String message;
}
