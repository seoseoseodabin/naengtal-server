package com.example.naengtal.domain.ingredient.controller;

import com.example.naengtal.domain.ingredient.dto.IngredientRequestDto;
import com.example.naengtal.domain.ingredient.dto.IngredientResponseDto;
import com.example.naengtal.domain.ingredient.service.IngredientService;
import com.example.naengtal.domain.member.entity.Member;
import com.example.naengtal.global.common.annotation.LoggedInUser;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("ingredient/")
@Slf4j
public class IngredientApiController {

    private final IngredientService ingredientService;

    @PostMapping(value = "add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveIngredient(@Parameter(hidden = true) @LoggedInUser Member member,
                                                 @RequestPart("image") MultipartFile image,
                                                 @Validated @RequestPart("dto") IngredientRequestDto ingredientRequestDto) {
        ingredientService.save(member, image, ingredientRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }

    @GetMapping("get")
    public ResponseEntity<List<IngredientResponseDto>> getIngredients(@Parameter(hidden = true) @LoggedInUser Member member) {
        List<IngredientResponseDto> ingredients = ingredientService.getIngredients(member);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ingredients);
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteIngredient(@Parameter(hidden = true) @LoggedInUser Member member,
                                                   @RequestParam(name = "id") int ingredientId) {
        ingredientService.deleteIngredient(member, ingredientId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("success");
    }

    @GetMapping("search")
    public ResponseEntity<List<String>> searchCategory(@RequestParam(name = "category") String category) {
        List<String> categoryList = ingredientService.search(category);
        log.debug(categoryList.toString());
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryList);
    }
}
