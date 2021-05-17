package com.example.recipecalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SaveRecipeAlertDialog extends DialogFragment {

    View view;
    EditText recipeNameEditText;
    RecipeSaver recipeSaver;
    int servings;

    public SaveRecipeAlertDialog(RecipeSaver recipeSaver, int servings) {
        super();
        this.recipeSaver = recipeSaver;
        this.servings = servings;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        view = getLayoutInflater().inflate(R.layout.save_recipe_dialog_alert, null);

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recipeNameEditText = view.findViewById(R.id.recipe_name_save);
                        String recipeName = recipeNameEditText.getText().toString();

                        Boolean success = false;

                        try {
                            MainActivity.database
                                    .recipesDao()
                                    .saveRecipe(recipeName, servings);

                            int recipeId = MainActivity.database
                                    .recipesDao().getRecipeId(recipeName);

                            recipeSaver.persistIngredients(recipeId);

                            Toast.makeText(getContext(), "Recipe Saved", Toast.LENGTH_LONG).show();

                            success = true;
                        }
                        catch (Exception e){
                        } finally {
                            if(!success) {
                                Toast.makeText(getContext(), "Recipe name already exists", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                return builder.create();
    }

    public interface RecipeSaver {
        void persistIngredients(int recipeId);
    }
}
