package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.FoodItemDetails;
import com.example.fsc_diner.controller.MainActivity;
import com.example.fsc_diner.controller.MainActivityManagerSide;
import com.example.fsc_diner.model.FoodItemInfo;
import com.squareup.picasso.Picasso;


import java.util.List;

public class FoodItemsAdapter extends RecyclerView.Adapter<FoodItemsAdapter.ViewHolder> {

    private List<FoodItemInfo> _items;
    private Context _context;
    private String _resName;

    public FoodItemsAdapter(Context context, List<FoodItemInfo> items, String resName) {

        this._context = context;
        this._items = items;
        this._resName = resName;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView _foodImage;
        public TextView _foodName;
        public RatingBar _foodRating;
        public TextView _foodCalories;
        public ImageButton _addBtn;
        public Button _priceBtn;
        public CardView _cardview;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _foodImage = (ImageView) itemView.findViewById(R.id.food_pic);
            _foodName = (TextView) itemView.findViewById(R.id.food_name);
            _foodRating = (RatingBar) itemView.findViewById(R.id.food_rating);
            _foodCalories = (TextView) itemView.findViewById(R.id.food_calorie);
            _addBtn = (ImageButton) itemView.findViewById(R.id.add_button);
            _priceBtn = (Button) itemView.findViewById(R.id.price_button);
            _cardview = (CardView)itemView.findViewById(R.id.cv);

        }
    }

    @NonNull
    @Override
    public FoodItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_food_menu, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemsAdapter.ViewHolder holder, final int position) {

        final String tempName= _items.get(position).getItemName();
        final float tempRating= Float.valueOf(_items.get(position).getItemRating());
        final String tempCalories= Integer.toString(_items.get(position).getItemCalories());
        final String tempPrice= Double.toString(_items.get(position).getItemPrice());
        final String tempkey= _items.get(position).getItemKey();
        final String tempResKey= _items.get(position).getRestaurantKey();

        holder._foodName.setText(tempName);
        holder._foodCalories.setText(tempCalories);
        holder._foodRating.setRating(tempRating);
        holder._foodName.setText(tempName);
        holder._priceBtn.setText("$" + tempPrice);


        Picasso.get()
                .load(_items.get(position).getItemImage())
                .into(holder._foodImage);


        holder._addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //do something
            }
        });

        holder._cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(_context, FoodItemDetails.class);
                i.putExtra("FoodItemName", tempName);
                i.putExtra("FoodItemKey", tempkey);
                i.putExtra("RestaurantKey", tempResKey);
                i.putExtra("RestaurantName", _resName);
                i.putExtra("ItemImage", _items.get(position).getItemImage());
                _context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return _items.size();
    }
}
