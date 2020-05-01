package com.example.fsc_diner.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.fsc_diner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivityEmployee extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employee);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_active_orders, R.id.navigation_account, R.id.navigation_closed_orders)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_employee);

        NavigationUI.setupWithNavController(navView, navController);
    }
}
