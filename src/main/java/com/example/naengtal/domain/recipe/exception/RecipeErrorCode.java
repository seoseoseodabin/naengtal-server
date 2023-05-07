package com.example.naengtal.domain.recipe.exception;

import com.example.naengtal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecipeErrorCode implements ErrorCode {

    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "recipe not found"),
    ;

    private HttpStatus httpStatus;
    private String message;
}