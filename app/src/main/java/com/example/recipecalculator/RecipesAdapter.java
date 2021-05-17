package com.example.recipecalculator;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.recipecalculator.R.drawable.recipe_row_colour;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> implements Filterable {

    List<Recipe> recipeList = new ArrayList<>();
    List<Recipe> filteredRecipes;
    public Context parentContext;

    @Override
    public Filter getFilter() {
        return new RecipeFilter();
    }

    private class RecipeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint == null || constraint.length() == 0){
                filterResults.values = recipeList;
                filterResults.count = recipeList.size();
            }
            else {
                List<Recipe> filteredRecipes = new ArrayList<>();
                for (int i = 0; i < recipeList.size(); i++) {
                    if (recipeList.get(i).recipe_name
                            .toLowerCase()
                            .contains(constraint.toString()
                                    .toLowerCase())) {
                        filteredRecipes.add(recipeList.get(i));
                    }
                }
                filterResults.values = filteredRecipes;
                filterResults.count = filteredRecipes.size();
                Log.d("CS50", String.format("%d", filteredRecipes.size()));
            }
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredRecipes = new ArrayList<>();
            filteredRecipes = (List<Recipe>) results.values;
            notifyDataSetChanged();
        }
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder{


        public LinearLayout linearLayout;
        public TextView recipeName;
        public Button deleteButton;
        public Context parentContext;

        public RecipeViewHolder(View view, Context parentContext){
            super(view);
            this.linearLayout = view.findViewById(R.id.recycler_layout);
            this.recipeName = view.findViewById(R.id.recycler_textView);
            this.deleteButton = view.findViewById(R.id.recipe_delete_button);
            this.parentContext = parentContext;

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentName = (String) recipeName.getTag();
                    int recipeId = (int) deleteButton.getTag();

                    MainActivity.database
                            .recipesDao()
                            .deleteRecipeIngredients(recipeId);
                    MainActivity.database
                            .recipesDao()
                            .deleteRecipe(currentName);

                    MainActivity.recipesAdapter.reloadRecipes();
                }
            });

            recipeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentName = (String) recipeName.getTag();
                    Intent intent = new Intent(parentContext, LoadedRecipe.class);
                    intent.putExtra("recipeName", currentName);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public RecipesAdapter(Context parentContext) {
        super();
        this.parentContext = parentContext;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_recycler_row, parent, false);
        return new RecipeViewHolder(view, parentContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {

        String currentRecipeName;
        int recipeId;

        if (filteredRecipes == null){
            currentRecipeName = recipeList.get(position).recipe_name;
            recipeId = recipeList.get(position).recipe_id;
        }
        else {
            currentRecipeName = filteredRecipes.get(position).recipe_name;
            recipeId = filteredRecipes.get(position).recipe_id;
        }
        holder.recipeName.setText(currentRecipeName);
        holder.recipeName.setTag(currentRecipeName);
        holder.deleteButton.setTag(recipeId);
    }

    @Override
    public int getItemCount() {
        if (filteredRecipes == null) {
            return recipeList.size();
        }
        else {
            return filteredRecipes.size();
        }
    }

    public void reloadRecipes(){
        recipeList = MainActivity.database
                .recipesDao().loadRecipes();
        /*
        for (Recipe recipe : recipeList){
            recipe.recipe_name = recipe.recipe_name.substring(0,1).toUpperCase() + recipe.recipe_name.substring(1);
        }

         */
        notifyDataSetChanged();
        getFilter().filter(null);
    }
}