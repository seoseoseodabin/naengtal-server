package com.example.naengtal.domain.recipe.dao;

import com.example.naengtal.domain.recipe.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer> {

    List<RecipeIngredient> findByRecipeCodeOrderByIngredientNumber(int recipeCode);
}
