package com.example.iot_lab4_20223806.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iot_lab4_20223806.R;
import com.example.iot_lab4_20223806.models.Meal;

import java.util.List;

// Clase 5.1 diap. 5 — Adapter hereda de RecyclerView.Adapter
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals;
    private OnMealClickListener listener;

    // Interface para manejar el click
    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public MealAdapter(List<Meal> meals, OnMealClickListener listener) {
        this.meals = meals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        holder.tvMealName.setText(meal.getStrMeal());
        holder.tvMealId.setText("ID: " + meal.getIdMeal());

        // Glide para cargar imagen desde URL
        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .into(holder.ivMeal);

        // Click listener
        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    // Clase 5.1 diap. 5 — ViewHolder hereda de RecyclerView.ViewHolder
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMeal;
        TextView tvMealName;
        TextView tvMealId;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMeal = itemView.findViewById(R.id.ivMeal);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealId = itemView.findViewById(R.id.tvMealId);
        }
    }
}