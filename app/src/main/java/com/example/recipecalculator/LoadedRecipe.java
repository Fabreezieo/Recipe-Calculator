package com.example.recipecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class LoadedRecipe extends AppCompatActivity {

    private RecipeIngredients recipePackage;
    private List<Ingredient> recipeIngredients;
    private Intent intent;
    private String recipeName;
    private int servingsNumber;

    private EditText servingsNumberEditText;
    private LinearLayout linearLayout;
    private TextView recipeNameTextView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loaded_recipe);
        //setupUI(findViewById(R.id.recipe_loaded_constrLayout));

        toolbar = findViewById(R.id.recipe_loaded_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        servingsNumberEditText = findViewById(R.id.recipe_editText);
        linearLayout = findViewById(R.id.recipe_scroll_linear_layout);

        intent = getIntent();
        recipeName = intent.getStringExtra("recipeName");
        toolbar.setTitle(recipeName);

        recipePackage = MainActivity.database.recipesDao().loadSelectedRecipe(recipeName);
        recipeIngredients = recipePackage.ingredients;
        servingsNumber = recipePackage.recipe.servings;

        servingsNumberEditText.setText(String.format("%d", servingsNumber));
        servingsNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int dynServings = 0;
                try {
                    dynServings = Integer.parseInt(s.toString());
                }
                catch (Exception e){
                }
                dynConversion(servingsNumber, dynServings);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        populateIngredients();
    }

    public void populateIngredients(){

        View view;
        TextView quantityTextView;
        TextView measureTextView;
        TextView nameTextView;
        double quantity;
        String measure;
        String ingredientName;

        for (int i = 0; i < recipeIngredients.size(); i++) {
            view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.converted_row, linearLayout, false);
            linearLayout.addView(view);

            quantityTextView = view.findViewById(R.id.conv_quantity);
            measureTextView = view.findViewById(R.id.conv_measure);
            nameTextView = view.findViewById(R.id.conv_name);

            quantity = recipeIngredients.get(i).quantity;
            measure = recipeIngredients.get(i).measure;
            ingredientName = recipeIngredients.get(i).name;

            quantityTextView.setText(String.format("%.1f", quantity));
            measureTextView.setText(measure);
            nameTextView.setText(ingredientName);
        }
    }

    public void dynConversion(int initServings, int newQuantity){
        for (int i = 0; i < this.linearLayout.getChildCount(); i++){
            TextView qTextView = linearLayout.getChildAt(i).findViewById(R.id.conv_quantity);
            double oneQ = recipeIngredients.get(i).quantity / (double) initServings;
            qTextView.setText(String.format("%.1f", oneQ * (double) newQuantity));
        }
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

        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}