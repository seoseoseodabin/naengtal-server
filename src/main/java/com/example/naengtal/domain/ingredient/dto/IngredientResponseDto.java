package com.example.naengtal.domain.ingredient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Getter
public class IngredientResponseDto {

    private int id;

    private String name;

    private String category;

    private String image;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate expirationDate;

    private boolean expirationDatePassed;
}
