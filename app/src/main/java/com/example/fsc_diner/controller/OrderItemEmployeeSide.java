package com.example.fsc_diner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.EmployeeOrderItemsAdapter;
import com.example.fsc_diner.model.OrderItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ng.max.slideview.SlideView;

public class OrderItemEmployeeSide extends AppCompatActivity {

    private int orderId;
    private int orderStatus;
    private String empName;
    private String employeeOrderKey = "";
    private String resKey = "";
    private String customerUid = "";
    private boolean isVisitor = true;
    private String handledBy = "";

    private ImageButton toolbarBackButton;
    private TextView orderIdTv;
    private TextView customerNameTv;
    private TextView customerEmailTv;
    private TextView handledByTv;
    private TextView statusTv;
    private SlideView slideButton;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<OrderItem> employeeOrderItems = new ArrayList<OrderItem>();
    private List<OrderItem> checkedItemList = new ArrayList<OrderItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_employee_side);

        orderId = getIntent().getIntExtra("OrderId", 0);
        orderStatus = getIntent().getIntExtra("OrderStatus", 3);
        employeeOrderKey = getIntent().getStringExtra("EmployeeOrderKey");
        resKey = getIntent().getStringExtra("RestaurantKey");
        customerUid = getIntent().getStringExtra("CustomerUid");
        empName = getIntent().getStringExtra("EmpName");

        if(getIntent().hasExtra("HandledBy")){
            if(getIntent().getStringExtra("HandledBy").equals(getIntent().getStringExtra("EmpName")))
                isVisitor = false;
        }
        else if(getIntent().getIntExtra("OrderStatus", 0) == 2)
            isVisitor = false;

        orderIdTv = findViewById(R.id.employee_order_items_orderId);
        customerNameTv = findViewById(R.id.employee_order_items_customer_name);
        customerEmailTv = findViewById(R.id.employee_order_items_customer_email);
        handledByTv = findViewById(R.id.employee_order_items_handledBy);
        statusTv = findViewById(R.id.employee_order_items_status);
        slideButton = findViewById(R.id.employee_order_items_slider_button);

        mRecyclerView = findViewById(R.id.employee_order_items_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(OrderItemEmployeeSide.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EmployeeOrderItemsAdapter(OrderItemEmployeeSide.this, employeeOrderItems, isVisitor, new EmployeeOrderItemsAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(OrderItem item) {
                checkedItemList.add(item);
            }

            @Override
            public void onItemUncheck(OrderItem item) {
                checkedItemList.remove(item);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        setToolbar();

        if(orderStatus == 4 || (orderStatus == 3 && isVisitor==true)) slideButton.setVisibility(View.GONE);

    }

    @Override
    protected void onStart(){
        super.onStart();

        populateOrderInfo();

        if(orderStatus == 4 || orderStatus == 3) {

            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(OrderItemEmployeeSide.this, MainActivityEmployee.class);
                    i.putExtra("ResKey", resKey);
                    i.putExtra("EmpName", empName);
                    startActivity(i);
                    finish();
                }
            });
        }
        else{
            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderItemEmployeeSide.this);
                    alertDialogBuilder.setMessage("Are you sure you don't want to do this order?");

                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    updateOrderStatus(resKey, employeeOrderKey, customerUid, 2);


                                    Intent i = new Intent(OrderItemEmployeeSide.this, MainActivityEmployee.class);
                                    i.putExtra("ResKey", resKey);
                                    i.putExtra("EmpName", empName);
                                    startActivity(i);
                                    finish();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }

        addEmployeeOrderItemsList();

        slideButton.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {

                onSlideButtonComplete();
            }
        });

    }

    private void setToolbar(){
        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.employee_order_items_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = myToolbar.findViewById(R.id.employee_order_items_toolbar_title);
        mTitle.setText(Integer.toString(orderId));

        toolbarBackButton = myToolbar.findViewById(R.id.employee_order_items_toolbar_back_btn);
    }

    private void updateOrderStatus(String resKey, String empOrderKey, final String customerUid, final int status){

        FirebaseDatabase.getInstance().getReference("Restaurant").child(resKey).child("Orders").child(empOrderKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    snapshot.getRef().child("status").setValue(status);
                    FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("CurrentOrders").child(snapshot.getKey()).child("status").setValue(status);

                    if(status == 2){
                        snapshot.getRef().child("handledBy").setValue("");
                        FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("CurrentOrders").child(snapshot.getKey()).child("handledBy").setValue("");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addEmployeeOrderItemsList(){

        FirebaseDatabase.getInstance().getReference("Restaurant").child(resKey).child("Orders").child(employeeOrderKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                employeeOrderItems.add(dataSnapshot.getValue(OrderItem.class));
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

    private void populateOrderInfo(){

        orderIdTv.setText(Integer.toString(orderId));

        FirebaseDatabase.getInstance().getReference("Users").child(customerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String customerFirstName = dataSnapshot.child("firstName").getValue(String.class);
                String customerLastName = dataSnapshot.child("lastName").getValue(String.class);
                String customerEmail = dataSnapshot.child("email").getValue(String.class);

                customerNameTv.setText(customerFirstName + " " + customerLastName);
                customerEmailTv.setText(customerEmail);

                dataSnapshot.getRef().child("CurrentOrders").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot subSnapshot) {

                        for(DataSnapshot subSubSnapshot: subSnapshot.getChildren()){

                            OrderItem tempItem = subSubSnapshot.getValue(OrderItem.class);

                            if(tempItem.getEmployeeOrderItemKey().equals(employeeOrderKey)){
                                handledByTv.setText(tempItem.getHandledBy());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(orderStatus == 4) statusTv.setText("Ready for Pick-Up");
        else statusTv.setText("In Progress");
    }

    private void onSlideButtonComplete(){

        if(employeeOrderItems.size() == checkedItemList.size()){

            slideButton.setSlideBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.slide_button_bg)));

            updateOrderStatus(resKey, employeeOrderKey, customerUid, 4);

            Intent i = new Intent(OrderItemEmployeeSide.this, MainActivityEmployee.class);
            i.putExtra("ResKey", resKey);
            startActivity(i);
            finish();

        }
        else{

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderItemEmployeeSide.this);
            alertDialogBuilder.setMessage("Please finish and check all the items in the order before swiping. Thank you!");

            alertDialogBuilder.setNegativeButton("Okay",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

}