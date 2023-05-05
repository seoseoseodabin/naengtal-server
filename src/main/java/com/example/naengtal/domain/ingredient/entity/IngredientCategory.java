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
    int category_id;

    @Column(name = "recipe_code")
    int recipe_code;

    @Column(name = "category")
    String category;
}
