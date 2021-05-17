package com.example.recipecalculator;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientVH> {

    private Context context;
    private IngredientVH.ItemDeleter deleter;
    private ArrayAdapter<CharSequence> adapter;

    public static class IngredientVH extends RecyclerView.ViewHolder {

        public EditText quantity;
        public Spinner unitOfMeasure;
        public EditText ingredientName;
        public Button deleteIngredient;
        public ItemDeleter deleter;

        public IngredientVH(View view, ItemDeleter deleter){
            super(view);
            this.quantity = view.findViewById(R.id.quantity);
            this.ingredientName = view.findViewById(R.id.ingredient_name);
            this.unitOfMeasure = view.findViewById(R.id.unitOfMeasure_spinner);
            this.deleteIngredient = view.findViewById(R.id.delete_icon);
            this.deleter = deleter;

            deleteIngredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient current = (Ingredient) quantity.getTag();
                    int position = MainActivity.ingredients.indexOf(current);
                    deleter.deleteIngredient(position);
                }
            });

            ingredientName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Ingredient current = (Ingredient) quantity.getTag();
                    current.name = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Ingredient current = (Ingredient) quantity.getTag();
                    try {
                        int newQuantity = Integer.parseInt(charSequence.toString());
                        current.quantity = (double) newQuantity;
                    } catch(NumberFormatException ex){

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            unitOfMeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Ingredient current = (Ingredient) quantity.getTag();
                    String m = (String) adapterView.getItemAtPosition(i);
                    current.measure = m;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }

        public interface ItemDeleter {
            void deleteIngredient(int position);
        }
    }

    public IngredientsAdapter(Context context, IngredientVH.ItemDeleter deleter){
        super();
        this.context = context;
        this.deleter = deleter;
        this.adapter = ArrayAdapter.createFromResource(context,
                R.array.units_of_measure, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public IngredientVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_row, parent, false);
        return new IngredientVH(view, deleter);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientVH holder, int position) {
        Ingredient current = MainActivity.ingredients.get(position);
        holder.quantity.setTag(current);

        holder.quantity.setText(String.format("%.0f", current.quantity));
        holder.ingredientName.setText(current.name);
        holder.unitOfMeasure.setAdapter(adapter);
        holder.unitOfMeasure.setSelection(adapter.getPosition(current.measure));
    }

    @Override
    public int getItemCount() {
        return MainActivity.ingredients.size();
    }

    public void reload(){
        MainActivity.ingredients = MainActivity.database.recipesDao().getNullIngredients();
        MainActivity.adapter.notifyDataSetChanged();
    }
}
