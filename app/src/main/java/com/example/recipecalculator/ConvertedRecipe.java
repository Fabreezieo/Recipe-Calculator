package com.example.recipecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ConvertedRecipe extends AppCompatActivity implements SaveRecipeAlertDialog.RecipeSaver {

    private LinearLayout linearLayout;
    private EditText editText;
    private Intent intent;

    private int targServings;
    private int initServings;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converted_recipe);
        //setupUI(findViewById(R.id.converted_constraintLayout));

        toolbar = findViewById(R.id.converted_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        initServings = intent.getIntExtra("initialServings", 0);
        targServings = intent.getIntExtra("targetServings", 0);

        linearLayout = findViewById(R.id.scroll_linear_layout);
        editText = findViewById(R.id.servings_editText);
        editText.setText(String.format("%d", targServings));

        calculateConversion(initServings, targServings);

        editText.addTextChangedListener(new TextWatcher() {
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
                dynConversion(initServings, dynServings);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void calculateConversion(int initServings, int targServings){
        View view;
        TextView quantityTextView;
        TextView measureTextView;
        TextView nameTextView;
        double oneQ;
        String measure;
        String name;

        for (int i = 0; i < MainActivity.ingredients.size(); i++){
            view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.converted_row, linearLayout, false);
            linearLayout.addView(view);

            quantityTextView = view.findViewById(R.id.conv_quantity);
            measureTextView = view.findViewById(R.id.conv_measure);
            nameTextView = view.findViewById(R.id.conv_name);

            oneQ = MainActivity.ingredients.get(i).quantity / (double) initServings;
            measure = MainActivity.ingredients.get(i).measure;
            name = MainActivity.ingredients.get(i).name;

            quantityTextView.setText(String.format("%.1f", (oneQ * (double) targServings)));
            measureTextView.setText(measure);
            nameTextView.setText(name);
        }
    }

    public void dynConversion(int initServings, int newQuantity){
        for (int i = 0; i < this.linearLayout.getChildCount(); i++){
            TextView qTextView = linearLayout.getChildAt(i).findViewById(R.id.conv_quantity);
            double oneQ = MainActivity.ingredients.get(i).quantity / (double) initServings;
            qTextView.setText(String.format("%.1f", (oneQ * (double) newQuantity)));
        }
    }

    public void saveRecipe(View view) {
        new SaveRecipeAlertDialog(this,
                Integer.parseInt(editText.getText().toString()))
                .show(getSupportFragmentManager(), "save recipe");
    }

    public void persistIngredients(int recipeId){

        Log.d("CS50", String.format("Saving recipe with id %d", recipeId));

        for (int i = 0; i < MainActivity.ingredients.size(); i++){
            View child = linearLayout.getChildAt(i);

            TextView q = child.findViewById(R.id.conv_quantity);
            TextView m = child.findViewById(R.id.conv_measure);
            TextView ing = child.findViewById(R.id.conv_name);

            double quantity = Double.parseDouble(q.getText().toString());
            String measure = m.getText().toString();
            String ingredient = ing.getText().toString();

            MainActivity.database.recipesDao().persistIngredients(quantity, measure, ingredient, recipeId);
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