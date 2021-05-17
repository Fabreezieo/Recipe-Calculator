package com.example.recipecalculator;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IngredientsAdapter.IngredientVH.ItemDeleter, SearchView.OnQueryTextListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;

    private RecyclerView ingredientsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private EditText initialServings;
    private EditText targetServings;

    private RecyclerView recipesRecyclerView;
    private RecyclerView.LayoutManager recipeLayoutManager;
    public static RecipesAdapter recipesAdapter;

    public static IngredientsAdapter adapter;
    public static List<Ingredient> ingredients = new ArrayList<>();
    public static RecipesDatabase database;

    public androidx.appcompat.widget.SearchView recipeSearchBar;
    private Context context;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        //setupUI(drawerLayout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        sharedPreferences = context
                .getSharedPreferences("com.example.recipecalculator.PREF_FILE_KEY", Context.MODE_PRIVATE);

        initialServings = findViewById(R.id.editTextNumber1);
        targetServings = findViewById(R.id.editTextNumber2);

        initialServings.setText(String.format("%d", sharedPreferences.getInt("mInitServ", 0)));
        targetServings.setText(String.format("%d", sharedPreferences.getInt("mTargServ", 0)));

        database = Room
                .databaseBuilder(getApplicationContext(), RecipesDatabase.class, "ingredients")
                .allowMainThreadQueries()
                .build();

        ingredientsRecyclerView = findViewById(R.id.ingredients_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        adapter = new IngredientsAdapter(this, this);
        ingredientsRecyclerView.setLayoutManager(layoutManager);
        ingredientsRecyclerView.setAdapter(adapter);

        adapter.reload();

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        recipesAdapter = new RecipesAdapter(this);
        recipeLayoutManager = new LinearLayoutManager(this);
        recipesRecyclerView.setLayoutManager(recipeLayoutManager);
        recipesRecyclerView.setAdapter(recipesAdapter);
        recipeSearchBar = findViewById(R.id.recipe_searchBar);
        recipesAdapter.reloadRecipes();
        recipeSearchBar.setOnQueryTextListener(this);

        if (ingredients.size() == 0){
            ingredients.add(new Ingredient(999, 0, "", ""));
            adapter.notifyDataSetChanged();
        }
    }

    public void addIngredient(View view) {
        ingredients.add(new Ingredient(999, 0, "", ""));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deleteIngredient(int position){
        ingredients.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.recipesDao().resetIngredients();
        database.recipesDao().saveArrayChanges(ingredients);

        int controlNumber = MainActivity.database.recipesDao().controlNumberOfIngredients();
        Log.d("CS50", String.format("Total Number of Ingredients is: %d", controlNumber));

        saveServings(Integer.parseInt(initialServings.getText().toString()),
                Integer.parseInt(targetServings.getText().toString()));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recipesAdapter.reloadRecipes();
    }

    public void goToconvertRecipe(View view) {

        int initServ = 0;
        int targServ = 0;
        Intent intent = new Intent(this, ConvertedRecipe.class);

        try {
            initServ = Integer.parseInt(initialServings.getText().toString());
            intent.putExtra("initialServings", initServ);
        }
        catch (Exception e) {
        }
        try {
            targServ = Integer.parseInt(targetServings.getText().toString());
            intent.putExtra("targetServings", targServ);
        }
        catch (Exception e) {
        }

        if (initServ != 0 && targServ != 0) {
            startActivity(intent);
        }
        else if (initServ == 0) {
            Toast.makeText(context, "Enter initial number of servings", Toast.LENGTH_LONG).show();
        }
        else if (targServ == 0) {
            Toast.makeText(context, "Enter target number of servings", Toast.LENGTH_LONG).show();
        }
    }

    public void saveServings(int initServ, int targServ){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mInitServ", initServ).apply();
        editor.putInt("mTargServ", targServ).apply();
    }

    public void resetIngredients(View view) {
        database.recipesDao().resetIngredients();
        adapter.reload();
        ingredients.add(new Ingredient(999, 0, "", ""));
        adapter.notifyDataSetChanged();
        initialServings.setText("0");
        targetServings.setText("0");
    }

    public void showRecipesFragment(View view) {
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        recipesAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recipesAdapter.getFilter().filter(newText);
        return false;
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        if (view instanceof ConstraintLayout) {

            for (int i = 0; i < ((ConstraintLayout) view).getChildCount(); i++) {

                View innerView = ((ConstraintLayout) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.END)){
            this.drawerLayout.closeDrawer(GravityCompat.END);
        }
        else {
            super.onBackPressed();
        }
    }
}