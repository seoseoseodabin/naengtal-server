package com.example.naengtal.domain.recipe.service;

import com.example.naengtal.domain.ingredient.dao.IngredientCategoryRepository;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import com.example.naengtal.domain.ingredient.entity.IngredientCategory;
import com.example.naengtal.domain.recipe.dao.RecipeInfoRepository;
import com.example.naengtal.domain.recipe.dao.RecipeIngredientRepository;
import com.example.naengtal.domain.recipe.dao.RecipeProcessRepository;
import com.example.naengtal.domain.recipe.dto.RecipeInfoResponseDto;
import com.example.naengtal.domain.recipe.entity.RecipeInfo;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.naengtal.domain.ingredient.exception.IngredientErrorCode.INGREDIENT_NOT_FOUND;
import static com.example.naengtal.domain.recipe.exception.RecipeErrorCode.RECIPE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeInfoRepository recipeInfoRepository;

    private final RecipeIngredientRepository recipeIngredientRepository;

    private final RecipeProcessRepository recipeProcessRepository;

    private final IngredientRepository ingredientRepository;

    private final IngredientCategoryRepository ingredientCategoryRepository;

    public List<RecipeInfoResponseDto> getRecipeInfoList(int ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RestApiException(INGREDIENT_NOT_FOUND));

        String category = ingredient.getCategory();
        List<Integer> recipeCodeList = ingredientCategoryRepository.findRecipeCodeByCategory(category);

        return recipeCodeList.stream()
                .map(recipeCode -> {
                    RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeCode)
                            .orElseThrow(() -> new RestApiException(RECIPE_NOT_FOUND));

                    return RecipeInfoResponseDto.builder()
                            .recipeCode(recipeCode)
                            .recipeName(recipeInfo.getRecipeName())
                            .summary(recipeInfo.getSummary())
                            .image(recipeInfo.getImage())
                            .build();
                }).collect(Collectors.toList());
    }
}
