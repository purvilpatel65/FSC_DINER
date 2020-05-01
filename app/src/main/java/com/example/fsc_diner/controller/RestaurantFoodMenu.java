package com.example.fsc_diner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.FoodItemsAdapter;
import com.example.fsc_diner.model.FoodItemInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class RestaurantFoodMenu extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<FoodItemInfo> itemList = new ArrayList<>();

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private String uid;

    private String restaurantKey;
    private String restaurantName;
    private ImageBadgeView badgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_food_menu);

        restaurantKey = getIntent().getStringExtra("RestaurantKey");
        restaurantName = getIntent().getStringExtra("RestaurantName");

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKey).child("Menu");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        setUpToolBar();

        mRecyclerView = (RecyclerView) findViewById(R.id.menu_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FoodItemsAdapter(getBaseContext(), itemList, restaurantName);
        mRecyclerView.setAdapter(mAdapter);

        addFoodMenuList();

    }

    private void addFoodMenuList(){

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                itemList.add(new FoodItemInfo(dataSnapshot.getValue(FoodItemInfo.class).getItemName(),
                                              dataSnapshot.getValue(FoodItemInfo.class).getItemImage(),
                                              dataSnapshot.getValue(FoodItemInfo.class).getItemPrice(),
                                              dataSnapshot.getKey(),
                                              restaurantKey,
                                              dataSnapshot.getValue(FoodItemInfo.class).getItemCalories(),
                                              dataSnapshot.getValue(FoodItemInfo.class).getItemRating()));

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setUpToolBar(){

        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_pressed);

        ((TextView)myToolbar.findViewById(R.id.food_menu_toolbar_title)).setText(restaurantName + " Menu");

        badgeView = (ImageBadgeView)myToolbar.findViewById(R.id.food_menu_badge_view);

        badgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("IsComingFromCartButton", true);
                startActivity(i);
            }
        });

        updateBadgeCartItem();

    }

    private void updateBadgeCartItem(){

        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                badgeView.setBadgeValue((int)dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
