package com.example.naengtal.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RecipeProcessResponseDto {

    private String description;

    private String image;

    private String tip;
}
