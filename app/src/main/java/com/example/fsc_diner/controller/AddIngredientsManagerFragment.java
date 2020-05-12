package com.example.fsc_diner.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.fsc_diner.R;
import com.example.fsc_diner.model.IngredientCategoryInfo;
import com.example.fsc_diner.model.IngredientSubItemInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AddIngredientsManagerFragment extends Fragment {

    private androidx.appcompat.widget.Toolbar myToolbar;
    private String foodItemName;
    private String restaurantName;
    private String restaurantKey;
    private Dialog addItemCategoryDialog;
    private Dialog addSubItemDialog;
    private DatabaseReference mDatabaseRef;
    private ExpandingList expandingList;
    private List<ExpandingItem> expandingItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_ingredients_manager_side, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab_food_ingredient);
        myToolbar = view.findViewById(R.id.my_toolbar_food_ingredients);
        foodItemName = getArguments().getString("ItemName");
        final String foodItemKey = getArguments().getString("ItemKey");
        restaurantName = getArguments().getString("RestaurantName");
        restaurantKey = getArguments().getString("RestaurantKey");
        addItemCategoryDialog = new Dialog(getContext());
        addSubItemDialog = new Dialog(getContext());

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKey).child("Menu").child(foodItemKey).child("Ingredients");
        expandingList = view.findViewById(R.id.expanding_list_main);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemCategoryDialog.setContentView(R.layout.dialog_add_ingredients_category);

                Button cancel = addItemCategoryDialog.findViewById(R.id.cancelIngredientCategoryButtonDialog);
                Button save = addItemCategoryDialog.findViewById(R.id.saveIngredientCategoryButtonDialog);
                final EditText ingredientCategoryName = addItemCategoryDialog.findViewById(R.id.dialog_ingredient_category_name);
                final RadioGroup selectionTypeGroup = addItemCategoryDialog.findViewById(R.id.radioSelectType);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addItemCategoryDialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int selectedId = selectionTypeGroup.getCheckedRadioButtonId();
                        RadioButton rb = addItemCategoryDialog.findViewById(selectedId);

                        IngredientCategoryInfo info = new IngredientCategoryInfo(ingredientCategoryName.getText().toString().trim(), rb.getText().toString());

                        String uploadId = mDatabaseRef.push().getKey();

                        mDatabaseRef.child(uploadId).setValue(info)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        ingredientCategoryName.setText("");
                                        ((RadioButton)addItemCategoryDialog.findViewById(R.id.radioSingleSelect)).setChecked(true);

                                        Toast.makeText(getContext(), "Successfully uploaded", Toast.LENGTH_LONG).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(getContext(), "Uploading Unsuccessful! Please Try Again!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

                addItemCategoryDialog.show();
                addItemCategoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addItemCategoryDialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        setToolbar();
        addIngredientCategoryList();
        addIngredientSubItemList();

        return view;
    }

    private void addIngredientCategoryList(){

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

                final ExpandingItem tempItem = expandingList.createNewItem(R.layout.custom_expanding_layout);
                ((TextView)tempItem.findViewById(R.id.title)).setText(dataSnapshot.getValue(IngredientCategoryInfo.class).getIngredientCategoryName());
                ((TextView)tempItem.findViewById(R.id.select_type)).setText(dataSnapshot.getValue(IngredientCategoryInfo.class).getIngredientCategorySelectionType());
                tempItem.setIndicatorColorRes(R.color.colorPrimary);
                tempItem.setIndicatorIconRes(R.drawable.ic_keyboard_arrow_up);

                tempItem.setStateChangedListener(new ExpandingItem.OnItemStateChanged() {
                    @Override
                    public void itemCollapseStateChanged(boolean expanded) {

                        if(expanded) tempItem.setIndicatorIconRes(R.drawable.ic_keyboard_arrow_down);
                        else tempItem.setIndicatorIconRes(R.drawable.ic_keyboard_arrow_up);

                    }
                });

                (tempItem.findViewById(R.id.add_more_sub_items)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddSubItemClick(tempItem, dataSnapshot.getKey());
                        //Toast.makeText(getApplicationContext(), "ID: " + expandingList.getItemByIndex(), Toast.LENGTH_LONG).show();
                    }
                });

                (tempItem.findViewById(R.id.edit_item)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onIngredientCategoryEdit(tempItem, ((TextView)tempItem.findViewById(R.id.title)).getText().toString(),((TextView)tempItem.findViewById(R.id.select_type)).getText().toString(), dataSnapshot.getKey());
                    }
                });

                (tempItem.findViewById(R.id.remove_item)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onIngredientCategoryDelete(tempItem, dataSnapshot.getKey());
                    }
                });

                expandingItems.add(tempItem);
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

    private  void addIngredientSubItemList(){

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int tempCount = 0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    String tempCategoryName = postSnapshot.getValue(IngredientCategoryInfo.class).getIngredientCategoryName();

                    ExpandingItem currentItem = expandingList.getItemByIndex(tempCount);

                    for(DataSnapshot subItemSnapshot : postSnapshot.child("Options").getChildren()){

                        View newSubItem = currentItem.createSubItem();

                        IngredientSubItemInfo info = subItemSnapshot.getValue(IngredientSubItemInfo.class);

                        configureSubItem(newSubItem, info.isPreSelected(), info.isHasExtraPrice(), info.getIngredientSubItemName(), new DecimalFormat("0.00").format(info.getExtraPrice()), currentItem, postSnapshot.getKey());
                    }

                    currentItem.collapse();
                    tempCount++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setToolbar(){
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = myToolbar.findViewById(R.id.toolbar_title_food_ingredients);
        mTitle.setText( foodItemName + " Ingredients");

        (myToolbar.findViewById(R.id.food_ingredients_toolbar_back_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("RestaurantName",restaurantName);
                args.putString("RestaurantKey",restaurantKey);
                FoodMenuManagerFragment fmf = new FoodMenuManagerFragment();
                fmf.setArguments(args);

                FragmentTransaction fragmentTransaction = ((AppCompatActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_manager, fmf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


    private void onIngredientCategoryEdit(final ExpandingItem item, final String _name, String _selectionType, final String ingredientCategoryKey) {

        addItemCategoryDialog.setContentView(R.layout.dialog_add_ingredients_category);

        Button cancel = addItemCategoryDialog.findViewById(R.id.cancelIngredientCategoryButtonDialog);
        Button save = addItemCategoryDialog.findViewById(R.id.saveIngredientCategoryButtonDialog);
        final EditText ingredientCategoryName = addItemCategoryDialog.findViewById(R.id.dialog_ingredient_category_name);
        final RadioGroup selectionTypeGroup = addItemCategoryDialog.findViewById(R.id.radioSelectType);


        ingredientCategoryName.setText(_name);

        if(_selectionType.equals("Multiple Select")) selectionTypeGroup.check(R.id.radioMultipleSelect);
        else if(_selectionType.equals("Single Select")) selectionTypeGroup.check(R.id.radioSingleSelect);
        else if(_selectionType.equals("No Select")) selectionTypeGroup.check(R.id.radioNoSelect);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemCategoryDialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = selectionTypeGroup.getCheckedRadioButtonId();
                RadioButton rb = addItemCategoryDialog.findViewById(selectedId);

                mDatabaseRef.child(ingredientCategoryKey).child("ingredientCategoryName").setValue(ingredientCategoryName.getText().toString().trim());
                mDatabaseRef.child(ingredientCategoryKey).child("ingredientCategorySelectionType").setValue(rb.getText().toString());

                ((TextView)item.findViewById(R.id.title)).setText(ingredientCategoryName.getText().toString().trim());
                ((TextView)item.findViewById(R.id.select_type)).setText(rb.getText().toString());

                ingredientCategoryName.setText("");
                ((RadioButton)addItemCategoryDialog.findViewById(R.id.radioSingleSelect)).setChecked(true);

                Toast.makeText(getContext(), "Successfuly changed", Toast.LENGTH_LONG).show();
            }
        });

        addItemCategoryDialog.show();
        addItemCategoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addItemCategoryDialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void onIngredientCategoryDelete(final ExpandingItem item, final String ingredientCategoryKey){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete this ingredient category?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mDatabaseRef.child(ingredientCategoryKey).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        expandingList.removeItem(item);
                                        Toast.makeText(getContext(), "Successfuly deleted", Toast.LENGTH_LONG).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(getContext(), "Deletion Unsuccessful!\n" + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void onAddSubItemClick(final ExpandingItem item, final String ingredientCategoryKey){

        addSubItemDialog.setContentView(R.layout.dialog_add_ingredient_sub_item);

        Button cancel = addSubItemDialog.findViewById(R.id.subItemCancelButtonDialog);
        Button save = addSubItemDialog.findViewById(R.id.subItemSaveButtonDialog);
        final EditText subItemName = addSubItemDialog.findViewById(R.id.dialog_sub_item_name);
        final EditText extraPrice = addSubItemDialog.findViewById(R.id.extra_price_sub_item);
        final CheckBox hasExtraPrice = addSubItemDialog.findViewById(R.id.has_extra_price_checkbox);
        final CheckBox isPreSelected = addSubItemDialog.findViewById(R.id.is_pre_selected);

        final String tempIngredientCategoryName = ((TextView)((ExpandingItem)item).findViewById(R.id.title)).getText().toString();

        hasExtraPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) extraPrice.setVisibility(View.VISIBLE);
                else extraPrice.setVisibility(View.INVISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSubItemDialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkHasExtraPrice = false;
                boolean checkIsPreSelected = false;

                if(hasExtraPrice.isChecked()) checkHasExtraPrice = true;
                if(isPreSelected.isChecked()) checkIsPreSelected = true;

                IngredientSubItemInfo info;

                if(checkHasExtraPrice == false) {
                    info = new IngredientSubItemInfo(subItemName.getText().toString().trim(), checkHasExtraPrice, checkIsPreSelected);
                } else{
                    info = new IngredientSubItemInfo(subItemName.getText().toString().trim(), Double.parseDouble(extraPrice.getText().toString().trim()), checkHasExtraPrice, checkIsPreSelected);
                }

                mDatabaseRef.child(ingredientCategoryKey).child("Options").child(subItemName.getText().toString().trim()).setValue(info)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                View newSubItem = item.createSubItem();
                                configureSubItem(newSubItem, isPreSelected.isChecked(), hasExtraPrice.isChecked(), subItemName.getText().toString().trim(), extraPrice.getText().toString().trim(), item, ingredientCategoryKey);

                                subItemName.setText("");
                                extraPrice.setText("");
                                hasExtraPrice.setChecked(false);
                                isPreSelected.setChecked(false);


                                Toast.makeText(getContext(), "Successfuly uploaded", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getContext(), "Uploading Unsuccessful! Please Try Again!\n" + e.toString(), Toast.LENGTH_LONG).show();

                            }
                        });
            }
        });

        addSubItemDialog.show();
        addSubItemDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addSubItemDialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void configureSubItem(final View subItemView, final boolean isPreselected, final boolean hasExtraPrice, final String name, final String extraPrice, final ExpandingItem item, final String ingredientCategoryKey){

        String selectedType = "";
        (subItemView.findViewById(R.id.extra_price_textview)).setVisibility(View.INVISIBLE);
        (subItemView.findViewById(R.id.selected_type_textview)).setVisibility(View.INVISIBLE);

        if(isPreselected) {
            (subItemView.findViewById(R.id.selected_type_textview)).setVisibility(View.VISIBLE);
            selectedType = "Pre-selected";
        }
        if(hasExtraPrice)(subItemView.findViewById(R.id.extra_price_textview)).setVisibility(View.VISIBLE);

        ((TextView) subItemView.findViewById(R.id.sub_title)).setText(name);
        ((TextView) subItemView.findViewById(R.id.extra_price_textview)).setText("($" + extraPrice + ")");
        ((TextView) subItemView.findViewById(R.id.selected_type_textview)).setText(selectedType);

        (subItemView.findViewById(R.id.edit_sub_items)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onEditSubItemClick(item, subItemView, isPreselected, hasExtraPrice, name, extraPrice, ingredientCategoryKey);
            }
        });

        (subItemView.findViewById(R.id.remove_sub_item)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemoveSubItemClick(item, subItemView, ingredientCategoryKey);
            }
        });
    }

    private void onEditSubItemClick(final ExpandingItem item, final View subItemView, boolean _isPreselected, boolean _hasExtraPrice, final String _name, String _extraPrice, final String itemCategoryKey){

        addSubItemDialog.setContentView(R.layout.dialog_add_ingredient_sub_item);

        Button cancel = addSubItemDialog.findViewById(R.id.subItemCancelButtonDialog);
        Button save = addSubItemDialog.findViewById(R.id.subItemSaveButtonDialog);
        final EditText subItemName = addSubItemDialog.findViewById(R.id.dialog_sub_item_name);
        final EditText extraPrice = addSubItemDialog.findViewById(R.id.extra_price_sub_item);
        final CheckBox hasExtraPrice = addSubItemDialog.findViewById(R.id.has_extra_price_checkbox);
        final CheckBox isPreSelected = addSubItemDialog.findViewById(R.id.is_pre_selected);

        final String tempIngredientCategoryName = ((TextView)((ExpandingItem)item).findViewById(R.id.title)).getText().toString();

        subItemName.setText(_name);
        extraPrice.setText(_extraPrice);
        hasExtraPrice.setChecked(_hasExtraPrice);
        isPreSelected.setChecked(_isPreselected);

        if(_hasExtraPrice) extraPrice.setVisibility(View.VISIBLE);

        hasExtraPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) extraPrice.setVisibility(View.VISIBLE);
                else extraPrice.setVisibility(View.INVISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSubItemDialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checkHasExtraPrice = false;
                boolean checkIsPreSelected = false;

                if(hasExtraPrice.isChecked()) checkHasExtraPrice = true;
                if(isPreSelected.isChecked()) checkIsPreSelected = true;

                final IngredientSubItemInfo info;

                if(!checkHasExtraPrice) {
                    info = new IngredientSubItemInfo(subItemName.getText().toString().trim(), checkHasExtraPrice, checkIsPreSelected);
                } else{
                    info = new IngredientSubItemInfo(subItemName.getText().toString().trim(), Double.parseDouble(extraPrice.getText().toString().trim()), checkHasExtraPrice, checkIsPreSelected);
                }

                mDatabaseRef.child(itemCategoryKey).child("Options").child(_name).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabaseRef.child(itemCategoryKey).child("Options").child(subItemName.getText().toString().trim()).setValue(info)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                item.removeSubItem(subItemView);

                                                View newSubItem = item.createSubItem();
                                                configureSubItem(newSubItem, isPreSelected.isChecked(), hasExtraPrice.isChecked(), subItemName.getText().toString().trim(), extraPrice.getText().toString().trim(), item, itemCategoryKey);

                                                subItemName.setText("");
                                                extraPrice.setText("");
                                                hasExtraPrice.setChecked(false);
                                                isPreSelected.setChecked(false);

                                                Toast.makeText(getContext(), "Successfuly changed", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(getContext(), "Uploading Unsuccessful! Please Try Again!\n" + e.toString(), Toast.LENGTH_LONG).show();

                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getContext(), "Uploading Unsuccessful! Please Try Again!\n" + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        addSubItemDialog.show();
    }

    private void onRemoveSubItemClick(final ExpandingItem item, final View subItemView, final String itemCategoryKey){

        final String tempIngredientCategoryName = ((TextView)((ExpandingItem)item).findViewById(R.id.title)).getText().toString();

        final String tempSubItemName = ((TextView) subItemView.findViewById(R.id.sub_title)).getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete this sub item?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mDatabaseRef.child(itemCategoryKey).child("Options").child(tempSubItemName).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        item.removeSubItem(subItemView);
                                        Toast.makeText(getContext(), "Successfuly deleted", Toast.LENGTH_LONG).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(getContext(), "Deletion Unsuccessful!\n" + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }
}
