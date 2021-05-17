package com.example.recipecalculator;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Recipe.class, Ingredient.class}, version = 1)
public abstract class RecipesDatabase extends RoomDatabase {
    public abstract RecipesDao recipesDao();
}
