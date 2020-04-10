package com.example.fsc_diner.ui.menu;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.RestaurantInfoAdapter;
import com.example.fsc_diner.controller.adapter.RestaurantOptionAdapter;
import com.example.fsc_diner.model.RestaurantInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RestaurantInfo> resInfo = new ArrayList<>();
    private RelativeLayout relativeLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
       // final TextView textView = root.findViewById(R.id.text_home);
        relativeLayout = root.findViewById(R.id.menu_relative);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant");

        mRecyclerView = root.findViewById(R.id.recyclerview_restaurant_customer_side);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new RestaurantOptionAdapter(getContext(), resInfo);
        mRecyclerView.setAdapter(mAdapter);

        addRestaurantList();


        final ImageView img = new ImageView(getContext());
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/fsc-diner.appspot.com/o/Images%2Ffood_background.jpg?alt=media&token=9eb41c03-8106-485c-8e96-a2de5e5c3d68").into(img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                relativeLayout.setBackgroundDrawable(img.getDrawable());
            }

            @Override
            public void onError(Exception e) {

            }

        });

        return root;
    }

    private void addRestaurantList(){


        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                resInfo.add(new RestaurantInfo(dataSnapshot.getValue(RestaurantInfo.class).getRestaurantname()
                        , dataSnapshot.getValue(RestaurantInfo.class).getRestaurantImage(), dataSnapshot.getKey()));


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
}