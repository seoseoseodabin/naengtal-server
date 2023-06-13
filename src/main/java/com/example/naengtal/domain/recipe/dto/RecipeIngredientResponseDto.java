package com.example.naengtal.domain.recipe.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RecipeIngredientResponseDto {

    private String name;

    private String amount;

    private String type;

    private boolean isContained;
}
