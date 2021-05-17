package com.example.recipecalculator;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients")
public class Ingredient {

    @PrimaryKey (autoGenerate = true)
    public int ingredientId;
    public int fromRecipe;
    public double quantity;
    public String measure;
    public String name;

    public Ingredient(int fromRecipe, double quantity, String measure, String name){
        this.fromRecipe = fromRecipe;
        this.quantity = quantity;
        this.measure = measure;
        this.name = name;
    }
}