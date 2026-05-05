package com.example.iot_lab4_20223806.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.iot_lab4_20223806.R;
import com.example.iot_lab4_20223806.databinding.ActivityAppBinding;

public class AppActivity extends AppCompatActivity {

    private ActivityAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);
        NavController navController = navHostFragment.getNavController();

        // Clase 6.2 diap. 19 — limpiar backstack al cambiar de tab
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
            NavigationUI.onNavDestinationSelected(item, navController);
            return true;
        });

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }
}