package com.example.fsc_diner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.fsc_diner.R;
import com.example.fsc_diner.model.CartItem;
import com.example.fsc_diner.model.FoodItemInfo;
import com.example.fsc_diner.model.IngredientCategoryInfo;
import com.example.fsc_diner.model.IngredientSubItemInfo;
import com.example.fsc_diner.ui.checkout.CheckOutFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ru.nikartm.support.ImageBadgeView;

public class FoodItemDetails extends AppCompatActivity {

    private String itemName;
    private String itemKey;
    private String resKey;
    private String resName;
    private String imageUrl;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private String uid;

    private ImageView itemImage;
    private TextView itemNameTextView;
    private TextView itemPrice;
    private Button addToCartBtn;
    private ImageButton removeQntBtn;
    private ImageButton addQntBtn;
    private Button qntBtn;
    private ImageBadgeView imgBadgeView;

    private ExpandingList expandingList;
    private double _tempExtraPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_details);

        itemName = getIntent().getStringExtra("FoodItemName");
        itemKey = getIntent().getStringExtra("FoodItemKey");
        resKey = getIntent().getStringExtra("RestaurantKey");
        resName = getIntent().getStringExtra("RestaurantName");
        imageUrl = getIntent().getStringExtra("ItemImage");

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(resKey).child("Menu").child(itemKey);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        setToolbar();

        expandingList = findViewById(R.id.item_details_expanding_list_main);

        itemImage = findViewById(R.id.item_details_image);
        itemNameTextView = findViewById(R.id.item_details_item_name);
        itemPrice = findViewById(R.id.item_details_item_price);
        addToCartBtn = findViewById(R.id.item_details_add_to_cart_btn);
        qntBtn = findViewById(R.id.item_details_quantity_btn);
        addQntBtn = findViewById(R.id.item_details_add_btn);
        removeQntBtn = findViewById(R.id.item_details_remove_btn);

        setUpDetails();
    }

    private void setToolbar(){

        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.item_details_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_pressed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = myToolbar.findViewById(R.id.item_details_toolbar_title);
        mTitle.setText(itemName);

        (myToolbar.findViewById(R.id.item_details_toolbar_back_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), RestaurantFoodMenu.class);
                i.putExtra("RestaurantKey", resKey);
                i.putExtra("RestaurantName", resName);
                startActivity(i);
            }
        });

        imgBadgeView = myToolbar.findViewById(R.id.item_details_badge_view);

        imgBadgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("IsComingFromCartButton", true);
                startActivity(i);
            }
        });

        updateBadgeCartItem();

    }

    private void setUpDetails(){

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                FoodItemInfo tempInfo = dataSnapshot.getValue(FoodItemInfo.class);

                itemNameTextView.setText(tempInfo.getItemName());
                itemPrice.setText("$" + new DecimalFormat("0.00").format(tempInfo.getItemPrice()));

                Picasso.get()
                        .load(tempInfo.getItemImage())
                        .into(itemImage);

                for(DataSnapshot itemSnapshot: dataSnapshot.child("Ingredients").getChildren()){


                    IngredientCategoryInfo tempCategoryInfo = itemSnapshot.getValue(IngredientCategoryInfo.class);

                    final ExpandingItem tempItem = expandingList.createNewItem(R.layout.custom_expanding_layout_customer_side);
                    tempItem.setIndicatorColorRes(R.color.colorPrimary);
                    tempItem.setIndicatorIconRes(R.drawable.ic_keyboard_arrow_up);

                    tempItem.setStateChangedListener(new ExpandingItem.OnItemStateChanged() {
                        @Override
                        public void itemCollapseStateChanged(boolean expanded) {

                            if(expanded == true) tempItem.setIndicatorIconRes(R.drawable.ic_keyboard_arrow_down);
                            else tempItem.setIndicatorIconRes(R.drawable.ic_keyboard_arrow_up);

                        }
                    });

                    ((TextView)tempItem.findViewById(R.id.item_details_title)).setText(tempCategoryInfo.getIngredientCategoryName());

                    if(tempCategoryInfo.getIngredientCategorySelectionType().equals("Single Select")) {

                        View subItemView = tempItem.createSubItem();
                        RadioGroup rg = (subItemView.findViewById(R.id.sub_item_radio_group));
                        (subItemView.findViewById(R.id.sub_item_check_box)).setVisibility(View.INVISIBLE);

                        for(DataSnapshot subItemSnapshot: itemSnapshot.child("Options").getChildren()){

                            IngredientSubItemInfo tempSubCategoryInfo = subItemSnapshot.getValue(IngredientSubItemInfo.class);

                            RadioButton radioBtn = new RadioButton(getApplicationContext());

                            if(tempSubCategoryInfo.isHasExtraPrice())
                            {
                                radioBtn.setText(tempSubCategoryInfo.getIngredientSubItemName() + " ( $" + tempSubCategoryInfo.getExtraPrice() + ")");


                            }
                            else{

                                radioBtn.setText(tempSubCategoryInfo.getIngredientSubItemName());
                            }

                            if(tempSubCategoryInfo.isPreSelected()) radioBtn.setChecked(true);

                            radioBtn.setId(View.generateViewId());

                            rg.addView(radioBtn);

                        }


                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                RadioButton tempBtn = (RadioButton)group.findViewById(checkedId);
                                String tempBtnText = tempBtn.getText().toString();
                                int index = tempBtnText.indexOf("$");


                                if(index != -1 && tempBtn.isChecked() == true){

                                    double tempItemPrice = Double.parseDouble(tempBtnText.substring(index+1, tempBtnText.length()-1));

                                    int tempQnt = Integer.parseInt(qntBtn.getText().toString());
                                    double tempPrice = Double.parseDouble(itemPrice.getText().toString().substring(1));
                                    _tempExtraPrice = tempItemPrice*tempQnt;

                                    itemPrice.setText("$" + (tempPrice + (tempItemPrice*tempQnt)));
                                }



                                if(index == -1 && tempBtn.isChecked() == true){

                                    int tempQnt = Integer.parseInt(qntBtn.getText().toString());
                                    double tempPrice = Double.parseDouble(itemPrice.getText().toString().substring(1));

                                    itemPrice.setText("$" + (tempPrice - _tempExtraPrice));
                                }

                            }
                        });

                    }
                    else{

                        for(DataSnapshot subItemSnapshot: itemSnapshot.child("Options").getChildren()){

                              View subItemView = tempItem.createSubItem();
                            final IngredientSubItemInfo tempSubCategoryInfo = subItemSnapshot.getValue(IngredientSubItemInfo.class);

                              (subItemView.findViewById(R.id.sub_item_radio_group)).setVisibility(View.INVISIBLE);

                              if(tempSubCategoryInfo.isHasExtraPrice()){

                                  ((CheckBox) subItemView.findViewById(R.id.sub_item_check_box)).setText(tempSubCategoryInfo.getIngredientSubItemName() + " ( $" + tempSubCategoryInfo.getExtraPrice() + ")");

                                  ((CheckBox) subItemView.findViewById(R.id.sub_item_check_box)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                      @Override
                                      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                          if(isChecked == true){

                                              int tempQnt = Integer.parseInt(qntBtn.getText().toString());
                                              double tempPrice = Double.parseDouble(itemPrice.getText().toString().substring(1));

                                              itemPrice.setText("$" + (tempPrice + (tempSubCategoryInfo.getExtraPrice()*tempQnt)));
                                          }

                                          if(isChecked == false){

                                              int tempQnt = Integer.parseInt(qntBtn.getText().toString());
                                              double tempPrice = Double.parseDouble(itemPrice.getText().toString().substring(1));

                                              itemPrice.setText("$" + (tempPrice - (tempSubCategoryInfo.getExtraPrice()*tempQnt)));
                                          }
                                      }
                                  });
                              }
                              else {
                                  ((CheckBox) subItemView.findViewById(R.id.sub_item_check_box)).setText(tempSubCategoryInfo.getIngredientSubItemName());
                              }

                              if(tempSubCategoryInfo.isPreSelected()) ((CheckBox)subItemView.findViewById(R.id.sub_item_check_box)).setChecked(true);
                        }
                    }


                    tempItem.collapse();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onQntRemovePressed(View view) {

        int tempQnt = Integer.parseInt(qntBtn.getText().toString());
        double tempPrice = Double.parseDouble(itemPrice.getText().toString().substring(1));

        if(tempQnt > 1){

            qntBtn.setText(Integer.toString(tempQnt - 1));
            itemPrice.setText("$" + Double.toString(tempPrice - (tempPrice/tempQnt)));

        }

    }

    public void onQntAddPressed(View view) {

        int tempQnt = Integer.parseInt(qntBtn.getText().toString());
        double tempPrice = Double.parseDouble(itemPrice.getText().toString().substring(1));

        qntBtn.setText(Integer.toString(tempQnt + 1));
        itemPrice.setText("$" + Double.toString(tempPrice + (tempPrice/tempQnt)));

    }

    public void onAddToCartPressed(View view) {

        CartItem cartItem = new CartItem();
        int itemCount = expandingList.getItemsCount();

        cartItem.setItemName(itemNameTextView.getText().toString());
        cartItem.setQuantity(Integer.parseInt(qntBtn.getText().toString()));
        cartItem.setTotalPrice(Double.parseDouble(itemPrice.getText().toString().substring(1)));
        cartItem.setRestaurantName(resName);
        cartItem.setImage(imageUrl);
        cartItem.setRestaurantKey(resKey);

        List<HashMap<String, List<String>>> ingredientsInfoList = new ArrayList<HashMap<String, List<String>>>();
        HashMap<String, List<String>> tempMap;

        for(int i = 0; i < itemCount; i++){

             tempMap = new HashMap<String, List<String>>();

            List<String> selectedIngredientsName = new ArrayList<String>();

            ExpandingItem tempItem = expandingList.getItemByIndex(i);
            String tempIngredientName = ((TextView)tempItem.findViewById(R.id.item_details_title)).getText().toString();

            int tempSubItemCount = tempItem.getSubItemsCount();

            for(int j = 0; j < tempSubItemCount; j++){

                View tempSubItemView = tempItem.getSubItemView(j);

                boolean checkIfSingleSelect = false;

                if(((RadioGroup)tempSubItemView.findViewById(R.id.sub_item_radio_group)).getVisibility() == View.VISIBLE)
                    checkIfSingleSelect = true;

                if(checkIfSingleSelect == true){

                    int tempRadioBtnId = ((RadioGroup)tempSubItemView.findViewById(R.id.sub_item_radio_group)).getCheckedRadioButtonId();
                    RadioButton tempRadioBtn = findViewById(tempRadioBtnId);

                    selectedIngredientsName.add(tempRadioBtn.getText().toString());
                }
                else{

                    CheckBox tempCheckBox = (tempSubItemView.findViewById(R.id.sub_item_check_box));

                    if(tempCheckBox.isChecked() == true)
                        selectedIngredientsName.add(tempCheckBox.getText().toString());
                }
            }

            tempMap.put(tempIngredientName, selectedIngredientsName);

            ingredientsInfoList.add(tempMap);
        }

        cartItem.setIngredients(ingredientsInfoList);

        String uploadKey = mDatabaseRef.push().getKey();
        cartItem.setCartItemKey(uploadKey);

        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Cart").child(uploadKey).setValue(cartItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateBadgeCartItem();
                        Toast.makeText(getApplicationContext(), "Successfully added item into cart!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), "Adding to cart unsuccessfull. Please try again!\n" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void updateBadgeCartItem(){

        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imgBadgeView.setBadgeValue((int)dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
