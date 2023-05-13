package com.example.naengtal.domain.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IngredientCountResponseDto {

    private int approachingIngredients;
    private int expiredIngredients;
}
