package com.example.recipecalculator;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeIngredients {
    @Embedded public Recipe recipe;
    @Relation(
            parentColumn = "recipe_id",
            entityColumn = "fromRecipe"
    )
    public List<Ingredient> ingredients;
}
