package com.example.fsc_diner.ui.checkout;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.CartItemAdapter;
import com.example.fsc_diner.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckOutFragment extends Fragment {

    private CheckOutViewModel checkOutViewModel;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private String uid;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CartItem> cartItem = new ArrayList<>();

    private Button clearCartButton;
    private Button payNowButton;
    private TextView subtotalTV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        checkOutViewModel =
                ViewModelProviders.of(this).get(CheckOutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_check_out, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        uid = mAuth.getUid();
        mRecyclerView = root.findViewById(R.id.cart_item_recycler_view);
        clearCartButton = root.findViewById(R.id.clear_cart_button);
        subtotalTV = root.findViewById(R.id.subtotalTV);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CartItemAdapter(getContext(), cartItem);
        mRecyclerView.setAdapter(mAdapter);

        addCartItemList();


        clearCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Cart").removeValue();
                mAdapter.notifyDataSetChanged();
                cartItem.clear();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Cart");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    double totalSum = 0.00;
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        double indSum = ds.child("totalPrice").getValue(Double.class).doubleValue();
                        totalSum = totalSum + indSum;
                    }

                    subtotalTV.setText("Your cart total is: $" + new DecimalFormat("0.00").format(totalSum));
                }else{
                    subtotalTV.setText("Your cart is currently empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void addCartItemList(){

        mDatabaseRef.child(uid).child("Cart").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                cartItem.add(dataSnapshot.getValue(CartItem.class));
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