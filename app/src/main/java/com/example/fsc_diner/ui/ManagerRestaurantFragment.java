package com.example.fsc_diner.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.AddRestaurantDialog;
import com.example.fsc_diner.controller.adapter.RestaurantInfoAdapter;
import com.example.fsc_diner.model.RestaurantInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class ManagerRestaurantFragment extends Fragment {

    private DatabaseReference mDatabaseRef;
    private RecyclerView.Adapter mAdapter;
    private List<RestaurantInfo> resInfo = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_manager_restaurant, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        RecyclerView mRecyclerView = root.findViewById(R.id.menu_list_chef_side);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RestaurantInfoAdapter(getContext(), resInfo);
        mRecyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Create and show the dialog.
                AddRestaurantDialog dialog = new AddRestaurantDialog();
                dialog.show(ft, "dialog");
            }
        });

        addRestaurantList();

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