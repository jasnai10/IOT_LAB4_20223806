package com.example.iot_lab4_20223806.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.iot_lab4_20223806.R;
import com.example.iot_lab4_20223806.adapters.MealAdapter;
import com.example.iot_lab4_20223806.databinding.FragmentMealsBinding;
import com.example.iot_lab4_20223806.models.Meal;
import com.example.iot_lab4_20223806.models.MealResponse;
import com.example.iot_lab4_20223806.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealsFragment extends Fragment {

    private FragmentMealsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMealsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Clase 6.2 diap. 25 — recibir Bundle con la categoría
        if (getArguments() != null && getArguments().containsKey("strCategory")) {
            String categoria = getArguments().getString("strCategory");
            // Ocultar campo de búsqueda si viene de Categories
            binding.etIngrediente.setVisibility(View.GONE);
            binding.btnBuscar.setVisibility(View.GONE);
            obtenerMealsPorCategoria(categoria);
        } else {
            // Mostrar campo de búsqueda si viene del BottomNav
            binding.etIngrediente.setVisibility(View.VISIBLE);
            binding.btnBuscar.setVisibility(View.VISIBLE);
            binding.btnBuscar.setOnClickListener(v -> {
                String ingrediente = binding.etIngrediente.getText().toString().trim();
                if (!ingrediente.isEmpty()) {
                    obtenerMealsPorIngrediente(ingrediente);
                } else {
                    Toast.makeText(getContext(), "Ingrese un ingrediente", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void obtenerMealsPorCategoria(String categoria) {
        RetrofitClient.getInstance().getMealsByCategory(categoria).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null
                        && !response.body().getMeals().isEmpty()) {
                    mostrarMeals(response.body().getMeals());
                } else {
                    Toast.makeText(getContext(),
                            "No se encontraron platos para esta categoría",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerMealsPorIngrediente(String ingrediente) {
        RetrofitClient.getInstance().getMealsByIngredient(ingrediente).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null
                        && !response.body().getMeals().isEmpty()) {
                    mostrarMeals(response.body().getMeals());
                } else {
                    // API devuelve meals:null cuando no encuentra resultados
                    Toast.makeText(getContext(),
                            "No se encontraron platos con ese ingrediente",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarMeals(List<Meal> meals) {
        MealAdapter adapter = new MealAdapter(meals, meal -> {
            // Pasar idMeal al RecipeFragment con Bundle
            Bundle bundle = new Bundle();
            bundle.putString("idMeal", meal.getIdMeal());
            NavController navController = NavHostFragment.findNavController(MealsFragment.this);
            navController.navigate(R.id.action_meals_to_recipe, bundle);
        });

        binding.rvMeals.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMeals.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}