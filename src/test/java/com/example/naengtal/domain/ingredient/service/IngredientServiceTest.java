package com.example.naengtal.domain.ingredient.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.ingredient.dto.IngredientRequestDto;
import com.example.naengtal.domain.ingredient.dto.IngredientResponseDto;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.service.S3Uploader;
import com.example.naengtal.global.error.RestApiException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.naengtal.domain.ingredient.exception.IngredientErrorCode.INGREDIENT_NOT_FOUND;
import static com.example.naengtal.global.error.CommonErrorCode.FORBIDDEN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private S3Uploader s3Uploader;

    private static Member member;

    private static Fridge fridge;

    private static IngredientRequestDto ingredientRequestDto;

    @BeforeAll
    static void before() {
        fridge = new Fridge(1);
        member = Member.builder()
                .id("test")
                .password("test1234")
                .fridge(fridge)
                .build();

        ingredientRequestDto = new IngredientRequestDto();
        ingredientRequestDto.setCategory("라면");
        ingredientRequestDto.setName("농심 너구리");
        ingredientRequestDto.setExpirationDate(LocalDate.now());
    }

    @Test
    @DisplayName("냉장고에 재료 추가 성공")
    public void add_ingredient_success() throws IOException {
        //Mock 파일생성
        MockMultipartFile image = new MockMultipartFile("image",
                "test.png",
                "image/png",
                new FileInputStream("src/test/image/test.jpg"));

        ingredientService.save(member, image, ingredientRequestDto);
    }

    @Test
    @DisplayName("재료 삭제 성공")
    public void delete_ingredient_success() {
        Ingredient ingredient = Ingredient.builder()
                .image("image")
                .name("ingredient")
                .fridge(fridge)
                .category("category")
                .expirationDate(LocalDate.now())
                .ingredientId(1)
                .build();
        given(ingredientRepository.findById(any(Integer.class))).willReturn(Optional.of(ingredient));

        ingredientService.deleteIngredient(member, 1);
    }

    @Test
    @DisplayName("없는 재료일 시 삭제 실패")
    public void delete_ingredient_fail_when_none() {
        given(ingredientRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        RestApiException restApiException = assertThrows(RestApiException.class,
                () -> ingredientService.deleteIngredient(member, 1));

        assertThat(restApiException.getErrorCode()).isEqualTo(INGREDIENT_NOT_FOUND);
    }

    @Test
    @DisplayName("본인 냉장고의 재료가 아닐 시 삭제 실패")
    public void delete_ingredient_fail_when_not_mine() {
        Ingredient ingredient = Ingredient.builder()
                .image("image")
                .name("ingredient")
                .fridge(new Fridge(2))
                .category("category")
                .expirationDate(LocalDate.now())
                .ingredientId(1)
                .build();
        given(ingredientRepository.findById(any(Integer.class))).willReturn(Optional.of(ingredient));

        RestApiException restApiException = assertThrows(RestApiException.class,
                () -> ingredientService.deleteIngredient(member, 1));

        assertThat(restApiException.getErrorCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    @DisplayName("냉장고 재료 조회 성공")
    public void get_ingredients_success() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                .image("image")
                .name("ingredient")
                .fridge(new Fridge(2))
                .category("category")
                .expirationDate(LocalDate.now())
                .ingredientId(1)
                .build());

        given(ingredientRepository.findByFridge(any(Fridge.class))).willReturn(ingredients);

        List<IngredientResponseDto> ingredientResponseDtos = ingredientService.getIngredients(member);

        assertThat(ingredientResponseDtos.size()).isEqualTo(1);
        assertThat(ingredientResponseDtos.get(0).getId())
                .isEqualTo(ingredients.get(0).getIngredientId());
        assertThat(ingredientResponseDtos.get(0).getName())
                .isEqualTo(ingredients.get(0).getName());
        assertThat(ingredientResponseDtos.get(0).getCategory())
                .isEqualTo(ingredients.get(0).getCategory());
        assertThat(ingredientResponseDtos.get(0).getImage())
                .isEqualTo(ingredients.get(0).getImage());
        assertThat(ingredientResponseDtos.get(0).getExpirationDate())
                .isEqualTo(ingredients.get(0).getExpirationDate());
    }
}