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
import android.widget.Toast;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.EmployeeOrdersAdapter;
import com.example.fsc_diner.model.OrderItem;
import com.example.fsc_diner.model.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivityEmployeeSide extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private String restaurantKey;
    private String uid;
    private String empName = "";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<OrderItem> employeeOrders = new ArrayList<OrderItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employee_side);

        restaurantKey = getIntent().getStringExtra("ResKey");

        if(getIntent().hasExtra("EmpName"))
           empName = getIntent().getStringExtra("EmpName");


        setToolbar();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKey).child("Orders");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        mRecyclerView = (RecyclerView)findViewById(R.id.employee_orders_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(MainActivityEmployeeSide.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EmployeeOrdersAdapter(MainActivityEmployeeSide.this, employeeOrders, empName);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart(){
        super.onStart();

        addEmployeeOrdersList();
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

    private void setToolbar(){
        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.employee_order_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = (TextView) myToolbar.findViewById(R.id.employee_order_toolbar_title);
        mTitle.setText("Orders");

        ((TextView)myToolbar.findViewById(R.id.employee_order_toolbar_logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent i = new Intent(v.getContext(), LoginActivity.class);
                Toast.makeText(v.getContext(), R.string.logout4, Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        });
    }

}
