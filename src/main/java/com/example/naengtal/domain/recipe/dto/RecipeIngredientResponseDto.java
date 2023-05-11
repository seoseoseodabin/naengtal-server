package com.example.naengtal.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RecipeIngredientResponseDto {

    private String name;

    private String amount;

    private String type;
}
