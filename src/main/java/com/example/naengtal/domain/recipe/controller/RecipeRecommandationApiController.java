package com.example.naengtal.domain.recipe.controller;

import com.example.naengtal.domain.recipe.dto.RecipeInfoResponseDto;
import com.example.naengtal.domain.recipe.dto.SpecificRecipeResponseDto;
import com.example.naengtal.domain.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("recipe/")
public class RecipeRecommandationApiController {

    private final RecipeService recipeService;

    @GetMapping("list")
    public ResponseEntity<List<RecipeInfoResponseDto>> getRecipeInfoList(@RequestParam("ingredientid") int ingredientId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.getRecipeInfoList(ingredientId));
    }

    @GetMapping("specific/{recipe_code}")
    public ResponseEntity<SpecificRecipeResponseDto> getSpecificRecipe(@PathVariable("recipe_code") int recipeCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(recipeService.getSpecificRecipe(recipeCode));
    }
}
