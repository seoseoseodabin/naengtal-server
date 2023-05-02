package com.example.naengtal.domain.ingredient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Setter
@Getter
public class IngredientRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private LocalDate expirationDate;
}
