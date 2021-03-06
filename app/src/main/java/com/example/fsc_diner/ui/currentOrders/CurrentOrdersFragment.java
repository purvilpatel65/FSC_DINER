package com.example.fsc_diner.ui.currentOrders;

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
import com.example.fsc_diner.controller.adapter.CurrentOrdersAdapter;
import com.example.fsc_diner.model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CurrentOrdersFragment extends Fragment {

    private DatabaseReference mDatabaseRef;
    private String uid;
    private RecyclerView.Adapter mAdapter;
    private List<OrderItem> currentOrderItem = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_current_orders, container, false);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        uid = mAuth.getUid();
        RecyclerView mRecyclerView = root.findViewById(R.id.current_order_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CurrentOrdersAdapter(getContext(), currentOrderItem);
        mRecyclerView.setAdapter(mAdapter);

        addCurrentOrdersList();

        return root;

    }

    private void addCurrentOrdersList(){
        mDatabaseRef.child(uid).child("CurrentOrders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                currentOrderItem.add(dataSnapshot.getValue(OrderItem.class));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                for (OrderItem tempItem : currentOrderItem){
                    if(tempItem.getCurrentOrderItemKey().equals(dataSnapshot.getKey())){
                        tempItem.setStatus(dataSnapshot.child("status").getValue(Integer.class));
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                currentOrderItem.remove(dataSnapshot.getValue(OrderItem.class));
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