package com.example.naengtal.domain.recipe.dao;

import com.example.naengtal.domain.recipe.entity.RecipeProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeProcessRepository extends JpaRepository<RecipeProcess, Integer> {

    List<RecipeProcess> findByRecipeCodeOrderByProcessNumber(int recipeCode);
}
