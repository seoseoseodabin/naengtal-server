package com.example.naengtal.domain.recipe.service;

import com.example.naengtal.domain.ingredient.dao.IngredientCategoryRepository;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.domain.recipe.dao.RecipeInfoRepository;
import com.example.naengtal.domain.recipe.dao.RecipeIngredientRepository;
import com.example.naengtal.domain.recipe.dao.RecipeProcessRepository;
import com.example.naengtal.domain.recipe.dto.RecipeInfoResponseDto;
import com.example.naengtal.domain.recipe.dto.RecipeIngredientResponseDto;
import com.example.naengtal.domain.recipe.dto.RecipeProcessResponseDto;
import com.example.naengtal.domain.recipe.dto.SpecificRecipeResponseDto;
import com.example.naengtal.domain.recipe.entity.RecipeInfo;
import com.example.naengtal.domain.recipe.entity.RecipeIngredient;
import com.example.naengtal.domain.recipe.entity.RecipeProcess;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
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

    public SpecificRecipeResponseDto getSpecificRecipe(Member member, int recipeCode) {
        Set<String> myIngredientSet = member.getFridge().getIngredients().stream()
                .map(Ingredient::getCategory)
                .collect(Collectors.toSet());

        // get info, ingredient, process from repository
        RecipeInfo info = recipeInfoRepository.findById(recipeCode)
                .orElseThrow(() -> new RestApiException(RECIPE_NOT_FOUND));

        List<RecipeIngredient> ingredientList = recipeIngredientRepository.findByRecipeCodeOrderByIngredientNumber(recipeCode);

        List<RecipeProcess> processList = recipeProcessRepository.findByRecipeCodeOrderByProcessNumber(recipeCode);

        // entity to dto
        List<RecipeIngredientResponseDto> mainIngredientDtoList = ingredientList.stream()
                .filter(ingredient -> ingredient.getIngredientType().equals("주재료"))
                .map(ingredient -> {
                    RecipeIngredientResponseDto dto = RecipeIngredientResponseDto.builder()
                                .name(ingredient.getIngredientName())
                                .amount(ingredient.getIngredientAmount())
                                .type(ingredient.getIngredientType())
                                .build();
                        if (myIngredientSet.contains(ingredient.getIngredientName()))
                            dto.setContained(true);
                        return dto;
                }).collect(Collectors.toList());

        List<RecipeIngredientResponseDto> additionalIngredientDtoList = ingredientList.stream()
                .filter(ingredient -> !ingredient.getIngredientType().equals("주재료"))
                .map(ingredient -> RecipeIngredientResponseDto.builder()
                        .name(ingredient.getIngredientName())
                        .amount(ingredient.getIngredientAmount())
                        .type(ingredient.getIngredientType())
                        .build())
                .collect(Collectors.toList());

        List<RecipeProcessResponseDto> processDtoList = processList.stream()
                .map(process -> RecipeProcessResponseDto.builder()
                        .description(process.getDescription())
                        .image(process.getProcessImage())
                        .tip(process.getTip())
                        .build())
                .collect(Collectors.toList());

        return SpecificRecipeResponseDto.builder()
                .name(info.getRecipeName())
                .summary(info.getSummary())
                .countryType(info.getCountryType())
                .type(info.getType())
                .time(info.getTime())
                .calories(info.getCalories())
                .amount(info.getRecipeAmount())
                .difficulty(info.getDifficulty())
                .mainIngredient(mainIngredientDtoList)
                .additionalIngredient(additionalIngredientDtoList)
                .process(processDtoList)
                .build();
    }
}
