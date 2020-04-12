package com.example.fsc_diner.ui.checkout;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckOutFragment extends Fragment {

    private CheckOutViewModel checkOutViewModel;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private String uid;
    private String userEmail;
    private String userName;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CartItem> cartItem = new ArrayList<>();

    private Button checkOutBtn;
    private boolean isVisisble = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        checkOutViewModel =
                ViewModelProviders.of(this).get(CheckOutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_check_out, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        checkOutBtn = (Button)root.findViewById(R.id.cart_item_check_out_button);
        checkOutBtn.setVisibility(View.GONE);

        uid = mAuth.getUid();
        mRecyclerView = (RecyclerView) root.findViewById(R.id.cart_item_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CartItemAdapter(getContext(), cartItem);
        mRecyclerView.setAdapter(mAdapter);

        addCartItemList();
        setUserEmail();

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckButtonClick();
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

                if(isVisisble==false && cartItem.size()>0){
                    checkOutBtn.setVisibility(View.VISIBLE);
                    isVisisble = true;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                if(isVisisble==true && cartItem.size()==0){
                    checkOutBtn.setVisibility(View.GONE);
                    isVisisble = false;
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
    private void setUserEmail(){
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