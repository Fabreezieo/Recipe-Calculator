package com.example.recipecalculator;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes", indices = {@Index(value = {"recipe_name"}, unique = true)})
public class Recipe {
    @PrimaryKey
    public int recipe_id;
    public String recipe_name;
    public int servings;
}
