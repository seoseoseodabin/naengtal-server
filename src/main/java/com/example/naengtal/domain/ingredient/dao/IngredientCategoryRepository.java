package com.example.naengtal.domain.ingredient.dao;

import com.example.naengtal.domain.ingredient.entity.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Integer> {

    @Query("select distinct c.category from IngredientCategory c where c.category like %:category% order by c.category")
    List<String> findByCategoryContainsOrderByCategory(@Param("category") String category);

    @Query("select c.recipeCode from IngredientCategory c where c.category like :category")
    List<Integer> findRecipeCodeByCategory(@Param("category") String category);
}