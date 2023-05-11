package com.example.naengtal.domain.recipe.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recipe_ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {

    @Id
    @Column(name = "ingredient_id")
    private int ingredientId;

    @Column(name = "recipe_code")
    private int recipeCode;

    @Column(name = "ingredient_number")
    private int ingredientNumber;

    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "ingredient_amount")
    private String ingredientAmount;

    @Column(name = "ingredient_type")
    private String ingredientType;
}
