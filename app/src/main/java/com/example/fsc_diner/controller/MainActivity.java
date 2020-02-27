package com.example.fsc_diner.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.fsc_diner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navView = (BottomNavigationView) findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_menu, R.id.navigation_account, R.id.navigation_CheckOutBag)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(navView, navController);

    }


    public void onClickMenuCardViews(View view) {

        String restaurantName = view.getTag().toString();

        Intent intent = new Intent(getApplicationContext(), RestaurantFoodMenu.class);
        intent.putExtra("RestaurantName", restaurantName);
        startActivity(intent);
    }
}
