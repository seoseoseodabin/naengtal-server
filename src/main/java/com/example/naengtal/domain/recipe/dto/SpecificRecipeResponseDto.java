package com.example.naengtal.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SpecificRecipeResponseDto {

    private String name;

    private String summary;

    private String countryType;

    private String type;

    private String time;

    private String calories;

    private String amount;

    private String difficulty;

    private List<RecipeIngredientResponseDto> ingredient;

    private List<RecipeProcessResponseDto> process;
}
