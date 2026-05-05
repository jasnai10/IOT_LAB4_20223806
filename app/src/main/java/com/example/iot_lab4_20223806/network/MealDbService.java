package com.example.iot_lab4_20223806.network;

import com.example.iot_lab4_20223806.models.CategoryResponse;
import com.example.iot_lab4_20223806.models.MealDetailResponse;
import com.example.iot_lab4_20223806.models.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealDbService {

    // Clase 4.2 diap. 15 — @GET con URL path
    // GET 1 — todas las categorías
    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    // GET 2A — meals por categoría
    @GET("filter.php")
    Call<MealResponse> getMealsByCategory(@Query("c") String category);

    // GET 2B — meals por ingrediente
    @GET("filter.php")
    Call<MealResponse> getMealsByIngredient(@Query("i") String ingredient);

    // GET 3 — detalle de un meal por ID
    @GET("lookup.php")
    Call<MealDetailResponse> getMealById(@Query("i") String id);

    // GET 4 — meal aleatorio (Pregunta 2)
    @GET("random.php")
    Call<MealDetailResponse> getRandomMeal();
}