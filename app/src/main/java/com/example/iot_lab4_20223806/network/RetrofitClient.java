package com.example.iot_lab4_20223806.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Clase 4.2 diap. 16 — baseUrl + GsonConverterFactory
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static MealDbService mealDbService;

    public static MealDbService getInstance() {
        if (mealDbService == null) {
            mealDbService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MealDbService.class);
        }
        return mealDbService;
    }
}