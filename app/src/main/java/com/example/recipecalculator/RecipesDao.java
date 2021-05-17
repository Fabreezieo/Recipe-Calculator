package com.example.recipecalculator;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RecipesDao {

    @Query("UPDATE ingredients SET quantity = :quantity")
    void updateQuantity(int quantity);

    @Query("UPDATE ingredients SET measure = :measure")
    void updateMeasure(String measure);

    @Query("UPDATE ingredients SET name = :name")
    void updateName(String name);

    @Query("SELECT * FROM ingredients WHERE fromRecipe = 999")
    List<Ingredient> getNullIngredients();

    @Insert
    void saveArrayChanges(List<Ingredient> ingredients);

    @Query("DELETE FROM ingredients WHERE fromRecipe = 999")
    void resetIngredients();

    @Query("SELECT * FROM recipes ORDER BY recipe_name")
    List<Recipe> loadRecipes();

    @Transaction
    @Query("SELECT * FROM recipes WHERE recipe_name = :recipeName")
    RecipeIngredients loadSelectedRecipe(String recipeName);


    @Query("INSERT INTO recipes (recipe_name, servings) VALUES (:recipeName, :servings)")
    void saveRecipe(String recipeName, int servings);

    //PROBABLY DON'T NEED IT!!!
    @Query("UPDATE ingredients SET fromRecipe = " +
            "(SELECT recipe_id FROM recipes WHERE recipe_name = :recipeName) WHERE fromRecipe = 999")
    void updateId(String recipeName);

    @Query("DELETE FROM ingredients WHERE fromRecipe = :recipeId")
    void deleteRecipeIngredients(int recipeId);

    @Query("SELECT recipe_id FROM recipes WHERE recipe_name = :recipeName")
    int getRecipeId(String recipeName);

    @Query("DELETE FROM recipes WHERE recipe_name = :recipeName")
    void deleteRecipe(String recipeName);

    @Query("INSERT INTO ingredients (quantity, measure, name, fromRecipe) VALUES(:quantities, :measures, :names, :fromRecipe)")
    void persistIngredients(double quantities, String measures, String names, int fromRecipe);



    @Query("SELECT * FROM ingredients")
    int controlNumberOfIngredients();
}
