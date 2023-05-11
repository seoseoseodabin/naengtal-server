package com.example.naengtal.domain.ingredient.service;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredient.dao.IngredientRepository;
import com.example.naengtal.domain.ingredient.dto.IngredientRequestDto;
import com.example.naengtal.domain.ingredient.dto.IngredientResponseDto;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import com.example.naengtal.domain.ingredient.dao.IngredientCategoryRepository;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.service.S3Uploader;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.naengtal.domain.ingredient.exception.IngredientErrorCode.INGREDIENT_NOT_FOUND;
import static com.example.naengtal.global.error.CommonErrorCode.FORBIDDEN;
import static com.example.naengtal.global.error.CommonErrorCode.INVALID_PARAMETER;

@Service
@Transactional
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final S3Uploader s3Uploader;
    private final IngredientCategoryRepository ingredientCategoryRepository;

    public void save(Member member, MultipartFile image, IngredientRequestDto ingredientRequestDto) {
        // 이미지 업로드
        if (image.isEmpty()) {
            throw new RestApiException(INVALID_PARAMETER);
        }
        String path = s3Uploader.upload(image);

        Ingredient ingredient = Ingredient.builder()
                .name(ingredientRequestDto.getName())
                .category(ingredientRequestDto.getCategory())
                .expirationDate(ingredientRequestDto.getExpirationDate())
                .fridge(member.getFridge())
                .image(path)
                .build();

        ingredientRepository.save(ingredient);
    }

    public List<IngredientResponseDto> getIngredients(Member member) {
        Fridge fridge = member.getFridge();

        return ingredientRepository.findByFridge(fridge)
                .stream()
                .map(ingredient -> IngredientResponseDto.builder()
                        .id(ingredient.getIngredientId())
                        .name(ingredient.getName())
                        .category(ingredient.getCategory())
                        .expirationDate(ingredient.getExpirationDate())
                        .image(ingredient.getImage())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteIngredient(Member member, int ingredientId) {
        // 재료가 존재하는지 확인
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new RestApiException(INGREDIENT_NOT_FOUND));

        // 본인 냉장고 재료인지 확인
        if (!member.getFridge().equals(ingredient.getFridge())) {
            throw new RestApiException(FORBIDDEN);
        }

        // 이미지 삭제
        s3Uploader.deleteFile(ingredient.getImage());

        ingredientRepository.delete(ingredient);
    }

    public List<String> search(String category) {
        return ingredientCategoryRepository.findByCategoryContainsOrderByCategory(category);
    }
}
