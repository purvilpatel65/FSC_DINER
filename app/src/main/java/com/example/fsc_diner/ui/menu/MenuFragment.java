package com.example.fsc_diner.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.RestaurantOptionAdapter;
import com.example.fsc_diner.model.RestaurantInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private DatabaseReference mDatabaseRef;
    private RecyclerView.Adapter mAdapter;
    private List<RestaurantInfo> resInfo = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private RelativeLayout relativeLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        relativeLayout = root.findViewById(R.id.menu_relative);

        String pathStream = "https://firebasestorage.googleapis.com/v0/b/fsc-diner.appspot.com/o/Images%2Ffood_background.jpg?alt=media&token=9eb41c03-8106-485c-8e96-a2de5e5c3d68";
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant");

        RecyclerView mRecyclerView = root.findViewById(R.id.recyclerview_restaurant_customer_side);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RestaurantOptionAdapter(getContext(), resInfo);
        mRecyclerView.setAdapter(mAdapter);

        addRestaurantList();

        final ImageView img = new ImageView(getContext());
        Picasso.get().load(pathStream).into(img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                relativeLayout.setBackground(img.getDrawable());
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
                keyList.add(dataSnapshot.getKey());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RestaurantInfo ri = dataSnapshot.getValue(RestaurantInfo.class);
                int index = keyList.indexOf(dataSnapshot.getKey());
                resInfo.set(index, ri);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = keyList.indexOf(dataSnapshot.getKey());
                resInfo.remove(index);
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
}