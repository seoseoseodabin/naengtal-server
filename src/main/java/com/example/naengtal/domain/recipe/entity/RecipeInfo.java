package com.example.naengtal.domain.recipe.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recipe_info")
@Getter
@Setter
@NoArgsConstructor
public class RecipeInfo {

    @Id
    @Column(name = "recipe_code")
    private int recipeCode;

    @Column(name = "recipe_name")
    private String recipeName;

    @Column(name = "summary")
    private String summary;

    @Column(name = "country_type")
    private String countryType;

    @Column(name = "type")
    private String type;

    @Column(name = "time")
    private String time;

    @Column(name = "calories")
    private String calories;

    @Column(name = "recipe_amount")
    private String recipeAmount;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "image")
    private String image;
}
