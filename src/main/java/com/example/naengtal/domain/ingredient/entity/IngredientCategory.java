package com.example.naengtal.domain.ingredient.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ingredient_category")
@Getter
@Setter
@NoArgsConstructor
public class IngredientCategory {

    @Id
    @Column(name = "category_id")
    int categoryId;

    @Column(name = "recipe_code")
    int recipeCode;

    @Column(name = "category")
    String category;
}
