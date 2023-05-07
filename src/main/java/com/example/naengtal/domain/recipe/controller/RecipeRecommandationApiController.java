package com.example.naengtal.domain.recipe.controller;

import com.example.naengtal.domain.recipe.dto.RecipeInfoResponseDto;
import com.example.naengtal.domain.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("recipe/")
public class RecipeRecommandationApiController {

    private final RecipeService recipeService;

    @GetMapping("")
    public ResponseEntity<List<RecipeInfoResponseDto>> getRecipeInfoList(@RequestParam("ingredientid") int ingredientId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.getRecipeInfoList(ingredientId));
    }
}
