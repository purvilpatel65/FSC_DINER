package com.example.fsc_diner.ui.checkout;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.fsc_diner.model.OrderItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Random;

public class CheckOutFragment extends Fragment {

    private CheckOutViewModel checkOutViewModel;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private String uid;
    private String userEmail;
    private String userName;

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
        payNowButton = root.findViewById(R.id.payNowButton);
        subtotalTV = root.findViewById(R.id.subtotalTV);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CartItemAdapter(getContext(), cartItem);
        mRecyclerView.setAdapter(mAdapter);

        addCartItemList();
        setUserEmail();


        clearCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Cart").removeValue();
                mAdapter.notifyDataSetChanged();
                cartItem.clear();
            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckButtonClick();
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

    private void onCheckButtonClick() {
        Random rand = new Random();
        final String orderId = String.format("%04d", rand.nextInt(10000));

        int index = userEmail.indexOf("@");
        String orderKey = orderId + "_" + userEmail.substring(0, index);

        for (final CartItem item : cartItem) {

            final String currentOrderKey = mDatabaseRef.child("CurrentOrders").push().getKey();

            final OrderItem orderItem = new OrderItem(Integer.parseInt(orderId)
                    , userName
                    , userEmail
                    , item.getItemName()
                    , item.getRestaurantName()
                    , item.getRestaurantKey()
                    , item.getQuantity()
                    , currentOrderKey
                    , orderKey
                    , 1
                    , item.getIngredients());

            FirebaseDatabase.getInstance().getReference("Restaurant").child(item.getRestaurantKey()).child("Orders").child(orderKey).child(currentOrderKey)
                    .setValue(orderItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mDatabaseRef.child(uid).child("CurrentOrders").child(currentOrderKey).setValue(orderItem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), item.getItemName() + " has successfully ordered. Thanks!", Toast.LENGTH_SHORT).show();
                                            cartItem.remove(item);
                                            mAdapter.notifyDataSetChanged();

                                            mDatabaseRef.child(uid).child("Cart").removeValue();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Unexpected error has occured. Please talk to the restaurant associates for further assistance!\n" + e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Unexpected error has occured. Please talk to the restaurant associates for further assistance!\n" + e.toString(), Toast.LENGTH_LONG).show();

                        }
                    });

        }
    }
    private void setUserEmail() {
        mDatabaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userEmail = dataSnapshot.child("email").getValue(String.class);
                userName = dataSnapshot.child("firstName").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}