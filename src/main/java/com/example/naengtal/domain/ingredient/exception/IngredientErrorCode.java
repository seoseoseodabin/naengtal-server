package com.example.naengtal.domain.ingredient.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum IngredientErrorCode implements ErrorCode {

    INGREDIENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "ingredient not found");

    private final HttpStatus httpStatus;
    private final String message;
}
