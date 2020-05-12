package com.example.fsc_diner.controller.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.model.CartItem;
import com.example.fsc_diner.model.FoodItemInfo;
import com.example.fsc_diner.model.HistoryItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context _context;
    private List<HistoryItem> _items;
    private String _customerUID;

    public HistoryAdapter(Context _context, List<HistoryItem> _items, String uid) {
        this._context = _context;
        this._items = _items;
        this._customerUID = uid;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView _itemName;
        public TextView _restaurantName;
        public TextView _orderDate;
        public TextView _ingredients;
        public Button _rate;
        public Button _reorder;
        public Button _remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _ingredients = itemView.findViewById(R.id.customer_history_ingredients);
            _itemName = itemView.findViewById(R.id.customer_history_item_name);
            _restaurantName = itemView.findViewById(R.id.customer_history_restaurant_name);
            _orderDate = itemView.findViewById(R.id.customer_history_order_date);
            _rate = itemView.findViewById(R.id.customer_history_rate_button);
            _remove = itemView.findViewById(R.id.customer_history_remove_button);
            _reorder = itemView.findViewById(R.id.customer_history_reorder_button);
        }

    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_history, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, final int position) {

        holder._itemName.setText(_items.get(position).getItemName());
        holder._restaurantName.setText(_items.get(position).getRestaurantName());
        holder._orderDate.setText(_items.get(position).getOrderDate());

        String  finalIngredientsList = "";

        for(HashMap<String, List<String>> eachIngredient : _items.get(position).getIngredients()){

            for(String key: eachIngredient.keySet()){
                finalIngredientsList += key.toString().toUpperCase() + ":"+ "\n";
            }

            for(List<String> value: eachIngredient.values())
            {
                int i = 0;
                for(String eachValue: value){

                    int index = eachValue.indexOf("(");

                    if(i==0){
                        if(index == -1) finalIngredientsList += eachValue.toString();
                        else finalIngredientsList += eachValue.toString().substring(0, index);
                    }
                    else{
                        if(index == -1) finalIngredientsList += ", " + eachValue.toString();
                        else finalIngredientsList += ", " + eachValue.toString().substring(0, index);
                    }

                    i++;
                }

                finalIngredientsList += "\n\n";
                break;
            }

        }

        holder._ingredients.setText(finalIngredientsList);

        holder._rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRateClick(_items.get(position).getRestaurantKey(), _items.get(position).getItemName());
            }
        });

        holder._remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(_items.get(position).getHistoryItemkey(), _customerUID);
            }
        });

        holder._reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReorderClick(_items.get(position).getHistoryItemkey()
                              ,_items.get(position).getRestaurantKey()
                              ,_items.get(position).getItemName()
                              ,_items.get(position).getRestaurantName()
                              ,_items.get(position).getIngredients());
            }
        });

    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    private void onRateClick(final String resKey, final String itemName){

        final Dialog rateDialog = new Dialog(_context);
        rateDialog.setContentView(R.layout.dialog_rate_food_item);

        final RatingBar rateBar = (RatingBar)rateDialog.findViewById(R.id.rate_ratingbar);
        final TextView mRatingScale = (TextView) rateDialog.findViewById(R.id.rate_text);
        Button send = (Button) rateDialog.findViewById(R.id.rate_send_button);
        ImageButton cancel = (ImageButton)rateDialog.findViewById(R.id.rate_cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
            }
        });

        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) (Math.round(ratingBar.getRating()))) {
                    case 1:
                        mRatingScale.setText("Very bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome. I love it");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(rateBar.getRating() > 0.0){

                        FirebaseDatabase.getInstance().getReference("Restaurant").child(resKey).child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                                    if(snapshot.child("itemName").getValue(String.class).equals(itemName)){
                                        float tempRating = snapshot.child("itemRating").getValue(Float.class);

                                        if(tempRating == 0.0){
                                            snapshot.child("itemRating").getRef().setValue(rateBar.getRating())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(_context, "Thanks for the feedback!", Toast.LENGTH_SHORT).show();
                                                            rateDialog.dismiss();
                                                        }
                                                    });
                                        }
                                        else {
                                            snapshot.child("itemRating").getRef().setValue((tempRating + rateBar.getRating()) / 2)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(_context, "Thanks for the feedback!", Toast.LENGTH_SHORT).show();
                                                            rateDialog.dismiss();
                                                        }
                                                    });
                                        }
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        Toast.makeText(_context, "Rating cannot be zero or no star. Please give some rating!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rateDialog.show();
    }

    private void onDeleteClick(final String historyItemKey, final String customerUid){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setMessage("Are you sure you want to delete this item?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("History").child(historyItemKey)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(_context, "Item successfully deleted. Thanks!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(_context, "Something went wrong in deleting the item. Please try again!\n" + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void onReorderClick(String historyItemKey, final String resKey, final String itemName, final String resName, final List<HashMap<String, List<String>>> ingredientList){

        final Dialog reorderDialog = new Dialog(_context);
        reorderDialog.setContentView(R.layout.dialog_reorder_quantity);

        final Button qntBtn = (Button) reorderDialog.findViewById(R.id.reorder_quantity_btn);
        ImageButton add = (ImageButton)reorderDialog.findViewById(R.id.reorder_add_btn);
        ImageButton remove = (ImageButton)reorderDialog.findViewById(R.id.reorder_remove_btn);
        final ImageButton cancel = (ImageButton)reorderDialog.findViewById(R.id.reorder_cancel_button);
        final Button addToCart = (Button) reorderDialog.findViewById(R.id.reorder_add_to_cart_button);
        final FoodItemInfo foodItem = new FoodItemInfo();

        FirebaseDatabase.getInstance().getReference("Restaurant").child(resKey).child("Menu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if(snapshot.child("itemName").getValue(String.class).equals(itemName)){
                        foodItem.setItemPrice(snapshot.child("itemPrice").getValue(Double.class));
                        foodItem.setItemImage(snapshot.child("itemImage").getValue(String.class));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorderDialog.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempQnt = Integer.parseInt(qntBtn.getText().toString());

                if(tempQnt > 1)
                    qntBtn.setText(Integer.toString(tempQnt - 1));

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempQnt = Integer.parseInt(qntBtn.getText().toString());
                qntBtn.setText(Integer.toString(tempQnt + 1));
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uploadKey = FirebaseDatabase.getInstance().getReference("Users").child(_customerUID).child("Cart").push().getKey();

                CartItem cartItem = new CartItem(itemName
                                                ,resName
                                                ,resKey
                                                ,Integer.parseInt(qntBtn.getText().toString())
                                                ,foodItem.getItemPrice()*Integer.parseInt(qntBtn.getText().toString())
                                                ,uploadKey
                                                ,foodItem.getItemImage()
                                                ,ingredientList);

                FirebaseDatabase.getInstance().getReference("Users").child(_customerUID).child("Cart").child(uploadKey)
                        .setValue(cartItem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                reorderDialog.dismiss();
                                Toast.makeText(_context, "Item successfully added to cart!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(_context, "Something went wrong in adding item to cart. Please try again!\n" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        reorderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reorderDialog.show();
    }
}
