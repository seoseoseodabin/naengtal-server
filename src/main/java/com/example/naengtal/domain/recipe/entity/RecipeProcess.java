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
@Table(name = "recipe_process")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeProcess {

    @Id
    @Column(name = "process_id")
    private int processId;

    @Column(name = "recipe_code")
    private int recipeCode;

    @Column(name = "process_number")
    private int processNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "process_image")
    private String processImage;

    @Column(name = "tip")
    private String tip;
}
