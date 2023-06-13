package com.example.naengtal.domain.recipe.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredient.dao.IngredientCategoryRepository;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.domain.recipe.dao.RecipeInfoRepository;
import com.example.naengtal.domain.recipe.dao.RecipeIngredientRepository;
import com.example.naengtal.domain.recipe.dao.RecipeProcessRepository;
import com.example.naengtal.domain.recipe.dto.RecipeInfoResponseDto;
import com.example.naengtal.domain.recipe.dto.SpecificRecipeResponseDto;
import com.example.naengtal.domain.recipe.entity.RecipeInfo;
import com.example.naengtal.domain.recipe.entity.RecipeIngredient;
import com.example.naengtal.domain.recipe.entity.RecipeProcess;
import com.example.naengtal.global.error.RestApiException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.naengtal.domain.ingredient.exception.IngredientErrorCode.INGREDIENT_NOT_FOUND;
import static com.example.naengtal.domain.recipe.exception.RecipeErrorCode.RECIPE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeInfoRepository recipeInfoRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private RecipeProcessRepository recipeProcessRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientCategoryRepository ingredientCategoryRepository;

    private static Fridge fridge;

    private static Member member;

    private static RecipeInfo info;

    private static List<RecipeIngredient> ingredientList;

    private static List<RecipeProcess> processList;

    @BeforeAll
    static void before() {
        fridge = new Fridge(1);
        fridge.setIngredients(Arrays.asList(new Ingredient(1, "동원참치", "참치통조림", LocalDate.now(), "imagelink", fridge)));

        member = Member.builder()
                .id("test")
                .password("test1234")
                .fridge(fridge)
                .build();

        info = new RecipeInfo(1, "참치김치찌개", "간단하고 맛있는 참치김치찌개", "한식",
                "찌개", "20분", "0Kcal", "2인분", "초보환영", "imagelink");

        ingredientList = Arrays.asList(new RecipeIngredient(1, 1, 1, "참치통조림", "1캔", "주재료"),
                new RecipeIngredient(2, 1, 2, "김치", "두 주먹", "주재료"),
                new RecipeIngredient(3, 1, 3, "소금", "약간", "부재료"));

        processList = Arrays.asList(new RecipeProcess(1, 1, 1, "김치를 적당히 썰어서 냄비에 넣고 볶는다", "imagelink", null),
                new RecipeProcess(2, 1, 2, "참치와 물과 소금을 넣고 팔팔 끓인다", "imagelink", "국물이 안 빨갛다면 김치국물 넣기"));
    }

    @Test
    @DisplayName("레시피 정보 리스트 가져오기 성공")
    void getRecipeInfoList_success() {
        // given
        Ingredient ingredient = Ingredient.builder()
                .ingredientId(1)
                .name("동원참치")
                .category("참치통조림")
                .build();
        List<Integer> recipeCodeList = Arrays.asList(1);

        given(ingredientRepository.findById(anyInt())).willReturn(Optional.of(ingredient));
        given(ingredientCategoryRepository.findRecipeCodeByCategory(any(String.class))).willReturn(recipeCodeList);
        given(recipeInfoRepository.findById(anyInt())).willReturn(Optional.of(info));

        // when
        int ingredientId = 1;
        List<RecipeInfoResponseDto> infoList = recipeService.getRecipeInfoList(ingredientId);

        // then
        assertEquals(infoList.size(), 1);
        assertEquals(infoList.get(0).getRecipeCode(), info.getRecipeCode());
        assertEquals(infoList.get(0).getRecipeName(), info.getRecipeName());
    }

    @Test
    @DisplayName("ingredientId에 해당하는 재료를 못 찾아서 레시피 정보 리스트 반환 실패")
    void getRecipeInfoList_fail_INGREDIENT_NOT_FOUND() {
        // given
        given(ingredientRepository.findById(anyInt())).willThrow(new RestApiException(INGREDIENT_NOT_FOUND));

        // when
        int ingredientId = 1;

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                recipeService.getRecipeInfoList(ingredientId)
        );
        assertEquals(exception.getErrorCode().getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(exception.getErrorCode().name(), "INGREDIENT_NOT_FOUND");
    }

    @Test
    @DisplayName("recipeCode에 해당하는 레시피가 없어서 레시피 정보 리스트 반환 실패")
    void getRecipeInfoList_fail_RECIPE_NOT_FOUND() {
        // given
        Ingredient ingredient = Ingredient.builder()
                .ingredientId(1)
                .name("동원참치")
                .category("참치통조림")
                .build();
        List<Integer> recipeCodeList = Arrays.asList(1);

        given(ingredientRepository.findById(anyInt())).willReturn(Optional.of(ingredient));
        given(ingredientCategoryRepository.findRecipeCodeByCategory(any(String.class))).willReturn(recipeCodeList);
        given(recipeInfoRepository.findById(anyInt())).willThrow(new RestApiException(RECIPE_NOT_FOUND));

        // when
        int ingredientId = 1;

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                recipeService.getRecipeInfoList(ingredientId)
        );
        assertEquals(exception.getErrorCode().getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(exception.getErrorCode().name(), "RECIPE_NOT_FOUND");
    }

    @Test
    @DisplayName("상세 레시피 가져오기 성공")
    void getSpecificRecipe_success() {
        // given
        given(recipeInfoRepository.findById(anyInt())).willReturn(Optional.of(info));
        given(recipeIngredientRepository.findByRecipeCodeOrderByIngredientNumber(anyInt())).willReturn(ingredientList);
        given(recipeProcessRepository.findByRecipeCodeOrderByProcessNumber(anyInt())).willReturn(processList);

        // when
        int recipeCode = 1;
        SpecificRecipeResponseDto dto = recipeService.getSpecificRecipe(member, recipeCode);

        // then
        assertEquals(dto.getName(), info.getRecipeName());
        assertEquals(dto.getSummary(), info.getSummary());

        assertEquals(dto.getMainIngredient().size(), 2);
        assertEquals(dto.getMainIngredient().get(0).getName(), ingredientList.get(0).getIngredientName());
        assertEquals(dto.getMainIngredient().get(1).getName(), ingredientList.get(1).getIngredientName());
        assertTrue(dto.getMainIngredient().get(0).isContained());
        assertFalse(dto.getMainIngredient().get(1).isContained());

        assertEquals(dto.getAdditionalIngredient().size(), 1);
        assertEquals(dto.getAdditionalIngredient().get(0).getName(), ingredientList.get(2).getIngredientName());

        assertEquals(dto.getProcess().size(), 2);
        assertEquals(dto.getProcess().get(0).getDescription(), processList.get(0).getDescription());
        assertEquals(dto.getProcess().get(1).getDescription(), processList.get(1).getDescription());
    }

    @Test
    @DisplayName("recipeCode에 해당하는 레시피 못 찾아서 상세 레시피 가져오기 실패")
    void getSpecificRecipe_fail_RECIPE_NOT_FOUND() {
        // given
        given(recipeInfoRepository.findById(anyInt())).willThrow(new RestApiException(RECIPE_NOT_FOUND));

        // when
        int recipeCode = 1;

        // then
        RestApiException exception = assertThrows(RestApiException.class, () ->
                recipeService.getSpecificRecipe(member, recipeCode)
        );
        assertEquals(exception.getErrorCode().getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(exception.getErrorCode().name(), "RECIPE_NOT_FOUND");
    }
}