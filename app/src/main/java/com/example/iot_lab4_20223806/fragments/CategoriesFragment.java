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
import com.example.iot_lab4_20223806.adapters.CategoryAdapter;
import com.example.iot_lab4_20223806.databinding.FragmentCategoriesBinding;
import com.example.iot_lab4_20223806.models.Category;
import com.example.iot_lab4_20223806.models.CategoryResponse;
import com.example.iot_lab4_20223806.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Clase 6.1 diap. 11 — Fragment con onCreateView + ViewBinding
public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtenerCategorias();
    }

    private void obtenerCategorias() {
        // Clase 4.2 diap. 16 — usando Retrofit
        RetrofitClient.getInstance().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call,
                                   @NonNull Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categorias = response.body().getCategories();
                    mostrarCategorias(categorias);
                } else {
                    Toast.makeText(getContext(), "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarCategorias(List<Category> categorias) {
        // Clase 5.1 — configurar RecyclerView con Adapter
        CategoryAdapter adapter = new CategoryAdapter(categorias, category -> {
            // Clase 6.2 diap. 25 — pasar dato con Bundle
            Bundle bundle = new Bundle();
            bundle.putString("strCategory", category.getStrCategory());

            // Clase 6.2 diap. 15 — NavController para navegar
            NavController navController = NavHostFragment.findNavController(CategoriesFragment.this);
            navController.navigate(R.id.action_categories_to_meals, bundle);
        });

        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCategories.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}