package com.example.iot_lab4_20223806.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.iot_lab4_20223806.R;
import com.example.iot_lab4_20223806.adapters.CategoryAdapter;
import com.example.iot_lab4_20223806.databinding.FragmentCategoriesBinding;
import com.example.iot_lab4_20223806.models.Category;
import com.example.iot_lab4_20223806.models.CategoryResponse;
import com.example.iot_lab4_20223806.models.MealDetail;
import com.example.iot_lab4_20223806.models.MealDetailResponse;
import com.example.iot_lab4_20223806.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Clase 5.2 diap. 26 — Fragment implementa SensorEventListener
public class CategoriesFragment extends Fragment implements SensorEventListener {

    private FragmentCategoriesBinding binding;

    // Clase 5.2 diap. 26 — variables del sensor
    private SensorManager mSensorManager;
    private Sensor mAcelerometro;

    // Última aceleración registrada para calcular el cambio brusco
    private float ultimaX = 0, ultimaY = 0, ultimaZ = 0;
    private boolean primeraLectura = true;
    private boolean dialogMostrado = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Clase 5.2 diap. 20 — instanciar SensorManager
        mSensorManager = (SensorManager) requireActivity()
                .getSystemService(Context.SENSOR_SERVICE);

        // Clase 5.2 diap. 21 — verificar si el sensor existe
        if (mSensorManager != null) {
            mAcelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAcelerometro == null) {
                Toast.makeText(getContext(),
                        "Su equipo no dispone de acelerómetro",
                        Toast.LENGTH_SHORT).show();
            }
        }

        obtenerCategorias();
    }

    // Clase 5.2 diap. 27 — registrar listener en onResume
    @Override
    public void onResume() {
        super.onResume();
        if (mSensorManager != null && mAcelerometro != null) {
            mSensorManager.registerListener(
                    this,
                    mAcelerometro,
                    SensorManager.SENSOR_DELAY_UI
            );
            primeraLectura = true;
            dialogMostrado = false;
        }
    }

    // Clase 5.2 diap. 27 — desregistrar listener en onStop
    @Override
    public void onStop() {
        super.onStop();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    // Clase 5.2 diap. 25 y 29 — onSensorChanged con values[0],[1],[2]
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // Clase 5.2 diap. 29 — values[0]=X, values[1]=Y, values[2]=Z
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Primera lectura solo guardamos valores base
            if (primeraLectura) {
                ultimaX = x;
                ultimaY = y;
                ultimaZ = z;
                primeraLectura = false;
                return;
            }

            // Calculamos el cambio brusco respecto a la lectura anterior
            float deltaX = Math.abs(x - ultimaX);
            float deltaY = Math.abs(y - ultimaY);
            float deltaZ = Math.abs(z - ultimaZ);

            // Umbral de 4 m/s² según enunciado del lab
            float umbral = 4.0f;

            if (!dialogMostrado &&
                    (deltaX > umbral ||
                            deltaY > umbral ||
                            deltaZ > umbral)) {
                dialogMostrado = true;
                obtenerRecetaAleatoria();
            }

            // Actualizamos la última lectura
            ultimaX = x;
            ultimaY = y;
            ultimaZ = z;
        }
    }

    // Clase 5.2 diap. 25 — obligatorio implementar onAccuracyChanged
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita implementar para este caso
    }

    private void obtenerRecetaAleatoria() {
        RetrofitClient.getInstance().getRandomMeal().enqueue(new Callback<MealDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealDetailResponse> call,
                                   @NonNull Response<MealDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getMeals() != null) {
                    MealDetail meal = response.body().getMeals().get(0);
                    mostrarDialogRecetaAleatoria(meal);
                } else {
                    dialogMostrado = false;
                    Toast.makeText(getContext(),
                            "Error al obtener receta aleatoria",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealDetailResponse> call,
                                  @NonNull Throwable t) {
                dialogMostrado = false;
                Toast.makeText(getContext(),
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogRecetaAleatoria(MealDetail meal) {
        if (getContext() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("🍽️ Plato Sorpresa")
                .setMessage(
                        "Nombre: " + meal.getStrMeal() + "\n\n" +
                                "Categoría: " + meal.getStrCategory() + "\n\n" +
                                "Origen: " + meal.getStrArea()
                )
                .setPositiveButton("Ver Receta completa", (dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("idMeal", meal.getIdMeal());
                    NavController navController =
                            NavHostFragment.findNavController(CategoriesFragment.this);
                    navController.navigate(R.id.recipeFragment, bundle);
                })
                .setNegativeButton("Cerrar", (dialog, which) -> {
                    dialogMostrado = false;
                })
                .setCancelable(false)
                .show();
    }

    private void obtenerCategorias() {
        RetrofitClient.getInstance().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call,
                                   @NonNull Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categorias = response.body().getCategories();
                    mostrarCategorias(categorias);
                } else {
                    Toast.makeText(getContext(),
                            "Error al obtener categorías",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call,
                                  @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Fallo de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarCategorias(List<Category> categorias) {
        CategoryAdapter adapter = new CategoryAdapter(categorias, category -> {
            Bundle bundle = new Bundle();
            bundle.putString("strCategory", category.getStrCategory());
            NavController navController =
                    NavHostFragment.findNavController(CategoriesFragment.this);
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