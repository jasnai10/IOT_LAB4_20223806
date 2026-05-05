package com.example.iot_lab4_20223806.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_lab4_20223806.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Clase 2.1 — setOnClickListener para el botón Ingresar
        binding.btnIngresar.setOnClickListener(v -> {
            if (tieneConexion()) {
                // Clase 2.1 — Intent para ir a AppActivity
                Intent intent = new Intent(MainActivity.this, AppActivity.class);
                startActivity(intent);
            } else {
                mostrarDialogSinConexion();
            }
        });
    }

    // Clase 4.2 diap. 6 — verificar conexión a internet
    private boolean tieneConexion() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean tieneConexion = false;
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager
                    .getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    tieneConexion = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    tieneConexion = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    tieneConexion = true;
                }
            }
        }
        return tieneConexion;
    }

    // Dialog cuando no hay conexión — enunciado del lab
    private void mostrarDialogSinConexion() {
        new AlertDialog.Builder(this)
                .setTitle("Sin conexión a Internet")
                .setMessage("Necesitas conexión a Internet para usar la aplicación.")
                .setPositiveButton("Configuración", (dialog, which) -> {
                    // Redirigir a ajustes del dispositivo — enunciado del lab
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}