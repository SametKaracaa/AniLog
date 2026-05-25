package com.malist.app;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.malist.app.data.prefs.PreferencesManager;
import com.malist.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Dynamic start destination
        PreferencesManager prefs = PreferencesManager.getInstance(this);
        NavGraph graph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        if (prefs.isFirstLaunch()) {
            graph.setStartDestination(R.id.onboardingFragment);
        } else {
            graph.setStartDestination(R.id.loginFragment);
        }
        navController.setGraph(graph);

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.loginFragment || id == R.id.registerFragment || id == R.id.onboardingFragment) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }
}
