package com.example.fsc_diner.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.fsc_diner.R;

public class RestaurantFoodMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_food_menu);

        String restaurantName = getIntent().getStringExtra("RestaurantName");

        TextView textview = findViewById(R.id.fakeText);
        textview.setText(restaurantName);
    }
}
