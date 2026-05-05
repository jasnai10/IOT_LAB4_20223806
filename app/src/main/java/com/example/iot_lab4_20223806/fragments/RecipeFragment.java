package com.example.iot_lab4_20223806.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.iot_lab4_20223806.databinding.FragmentRecipeBinding;
import com.example.iot_lab4_20223806.models.MealDetail;
import com.example.iot_lab4_20223806.models.MealDetailResponse;
import com.example.iot_lab4_20223806.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Si viene desde MealsFragment con un idMeal
        if (getArguments() != null && getArguments().containsKey("idMeal")) {
            String idMeal = getArguments().getString("idMeal");
            binding.etMealId.setText(idMeal);
            binding.etMealId.setEnabled(false);
            obtenerReceta(idMeal);
        }

        // Botón buscar manual por ID
        binding.btnBuscarReceta.setOnClickListener(v -> {
            String idMeal = binding.etMealId.getText().toString().trim();
            if (!idMeal.isEmpty()) {
                obtenerReceta(idMeal);
            } else {
                Toast.makeText(getContext(), "Ingrese un ID de plato", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerReceta(String idMeal) {
        RetrofitClient.getInstance().getMealById(idMeal).enqueue(new Callback<MealDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealDetailResponse> call,
                                   @NonNull Response<MealDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null) {
                    MealDetail meal = response.body().getMeals().get(0);
                    mostrarReceta(meal);
                } else {
                    Toast.makeText(getContext(), "Receta no encontrada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarReceta(MealDetail meal) {
        // Hacer visibles todos los elementos
        binding.ivMealDetail.setVisibility(View.VISIBLE);
        binding.tvMealName.setVisibility(View.VISIBLE);
        binding.tvMealCategory.setVisibility(View.VISIBLE);
        binding.tvMealArea.setVisibility(View.VISIBLE);
        binding.tvIngredientes.setVisibility(View.VISIBLE);
        binding.tvMealIngredients.setVisibility(View.VISIBLE);
        binding.tvInstrucciones.setVisibility(View.VISIBLE);
        binding.tvMealInstructions.setVisibility(View.VISIBLE);

        // Llenar datos
        binding.tvMealName.setText(meal.getStrMeal());
        binding.tvMealCategory.setText("Categoría: " + meal.getStrCategory());
        binding.tvMealArea.setText("Origen: " + meal.getStrArea());
        binding.tvMealInstructions.setText(meal.getStrInstructions());
        binding.tvMealIngredients.setText(
                "• " + meal.getStrIngredient1() + "\n" +
                        "• " + meal.getStrIngredient2() + "\n" +
                        "• " + meal.getStrIngredient3()
        );

        Glide.with(requireContext())
                .load(meal.getStrMealThumb())
                .into(binding.ivMealDetail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}