package com.example.naengtal.domain.recipe.dao;

import com.example.naengtal.domain.recipe.entity.RecipeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeInfoRepository extends JpaRepository<RecipeInfo, Integer> {
}
