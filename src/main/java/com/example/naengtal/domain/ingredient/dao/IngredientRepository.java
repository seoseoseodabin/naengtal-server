package com.example.naengtal.domain.ingredient.dao;

import com.example.naengtal.domain.fridge.entity.Fridge;
import com.example.naengtal.domain.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    List<Ingredient> findByFridge(Fridge fridge);

    List<Ingredient> findByFridgeOrderByIngredientIdDesc(Fridge fridge);

    List<Ingredient> findByFridgeOrderByExpirationDate(Fridge fridge);

    int countByFridgeAndExpirationDateLessThanEqual(Fridge fridge, LocalDate localDate);

    int countByFridgeAndExpirationDateBetween(Fridge fridge, LocalDate start, LocalDate end);

    List<Ingredient> findByExpirationDateLessThanEqualOrderByExpirationDate(LocalDate localDate);
}
