package com.example.naengtal.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class RecipeInfoResponseDto {

    private int recipeCode;

    private String recipeName;

    private String summary;

    private String image;
}
