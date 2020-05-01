package com.example.fsc_diner.ui.ActiveOrders;

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
import com.example.fsc_diner.controller.adapter.EmployeeOrdersAdapter;
import com.example.fsc_diner.model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class ActiveOrders extends Fragment {
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private String restaurantKey;
    private String uid;
    private String empName = "";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<OrderItem> employeeOrders = new ArrayList<OrderItem>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restaurantKey = getActivity().getIntent().getStringExtra("ResKey");

        if(getActivity().getIntent().hasExtra("EmpName"))
            empName = getActivity().getIntent().getStringExtra("EmpName");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKey).child("Orders");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        mRecyclerView = view.findViewById(R.id.employee_orders_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EmployeeOrdersAdapter(getContext(), employeeOrders, empName);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addEmployeeOrdersList(){

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                for(DataSnapshot subSnapShot:dataSnapshot.getChildren()){

                    OrderItem tempItem = subSnapShot.getValue(OrderItem.class);
                    employeeOrders.add(tempItem);
                    mAdapter.notifyDataSetChanged();

                    break;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                for (OrderItem tempItem : employeeOrders){
                    if(tempItem.getEmployeeOrderItemKey().equals(dataSnapshot.getKey())){

                        for(DataSnapshot subSnapshots: dataSnapshot.getChildren()){
                            tempItem.setStatus(subSnapshots.child("status").getValue(Integer.class));
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for (OrderItem tempItem : employeeOrders){
                    if(tempItem.getEmployeeOrderItemKey().equals(dataSnapshot.getKey())){
                        employeeOrders.remove(tempItem);
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

    @Override
    public void onStart(){
        super.onStart();

        addEmployeeOrdersList();
    }
}



