package com.example.fsc_diner.controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.FoodItemInfoAdapter;
import com.example.fsc_diner.model.FoodItemInfo;
import com.example.fsc_diner.ui.ManagerRestaurantFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoodMenuManagerFragment extends Fragment {

    private androidx.appcompat.widget.Toolbar myToolbar;
    private DatabaseReference mDatabaseRef;
    private RecyclerView.Adapter mAdapter;
    private List<FoodItemInfo> foodItemInfo = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private String restaurantKey;
    private String restaurantName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_menu_manager_side, container, false);
        myToolbar = view.findViewById(R.id.my_toolbar_food_menu);
        FloatingActionButton fab = view.findViewById(R.id.fab_food_menu);

        restaurantName = getArguments().getString("RestaurantName");
        restaurantKey = getArguments().getString("RestaurantKey");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKey).child("Menu");
        RecyclerView mRecyclerView = view.findViewById(R.id.food_menu_list_manager_side);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FoodItemInfoAdapter(getContext(), foodItemInfo, restaurantName);
        mRecyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("RestaurantName", restaurantName);
                args.putString("RestaurantKey",restaurantKey);
                AddFoodItemDialog dialog = new AddFoodItemDialog();
                dialog.setArguments(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "abc");
            }
        });

        setToolbar();
        addFoodItemList();
        return view;
    }

    public void addFoodItemList() {

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                foodItemInfo.add(new FoodItemInfo(dataSnapshot.getValue(FoodItemInfo.class).getItemName()
                        , dataSnapshot.getValue(FoodItemInfo.class).getItemImage()
                        , dataSnapshot.getValue(FoodItemInfo.class).getItemPrice()
                        , dataSnapshot.getKey()
                        , restaurantKey
                        , dataSnapshot.getValue(FoodItemInfo.class).getItemCalories()));

                keyList.add(dataSnapshot.getKey());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FoodItemInfo fi = dataSnapshot.getValue(FoodItemInfo.class);
                int index = keyList.indexOf(dataSnapshot.getKey());
                foodItemInfo.set(index, fi);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = keyList.indexOf(dataSnapshot.getKey());
                foodItemInfo.remove(index);
                keyList.remove(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setToolbar(){
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_pressed);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagerRestaurantFragment mrf = new ManagerRestaurantFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_manager, mrf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        TextView mTitle = myToolbar.findViewById(R.id.toolbar_title_food_menu);
        mTitle.setText( restaurantName + " Menu");
    }
}
