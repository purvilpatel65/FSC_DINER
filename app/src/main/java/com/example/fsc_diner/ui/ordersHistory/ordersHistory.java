package com.example.fsc_diner.ui.ordersHistory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.HistoryAdapter;
import com.example.fsc_diner.model.HistoryItem;
import com.example.fsc_diner.model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class ordersHistory extends Fragment {

    private RecyclerView.Adapter mAdapter;
    private List<HistoryItem> historyItems = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private String userUid;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders_history, container, false);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        userUid = FirebaseAuth.getInstance().getUid();

        RecyclerView mRecyclerView = root.findViewById(R.id.customer_history_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HistoryAdapter(getContext(), historyItems, FirebaseAuth.getInstance().getUid());
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onStart(){
        super.onStart();
        historyItems.clear();
        addHistoryItemList();
    }

    private void addHistoryItemList(){

        mDatabaseRef.child(userUid).child("History").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                OrderItem tempItem = dataSnapshot.getValue(OrderItem.class);

                historyItems.add(new HistoryItem(dataSnapshot.child("itemName").getValue(String.class)
                                                ,dataSnapshot.child("restaurantName").getValue(String.class)
                                                ,dataSnapshot.child("restaurantKey").getValue(String.class)
                                                ,dataSnapshot.child("OrderDate").getValue(String.class)
                                                ,dataSnapshot.getKey()
                                                ,tempItem.getIngredients()));

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for(HistoryItem tempItem: historyItems){

                    if(tempItem.getHistoryItemkey().equals(dataSnapshot.getKey())){
                        historyItems.remove(tempItem);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
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
