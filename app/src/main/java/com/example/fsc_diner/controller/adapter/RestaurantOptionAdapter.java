package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.RestaurantFoodMenu;
import com.example.fsc_diner.model.RestaurantInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantOptionAdapter extends RecyclerView.Adapter<RestaurantOptionAdapter.ViewHolder> {

    private Context _context;
    private List<RestaurantInfo> _restaurant;

    public RestaurantOptionAdapter(Context _context, List<RestaurantInfo> _restaurant) {
        this._context = _context;
        this._restaurant = _restaurant;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView _restaurantName;
        public ImageView _restaurantImage;
        public CardView _cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _restaurantName= (TextView) itemView.findViewById(R.id.cardview_res_name_customer_side);
            _restaurantImage= (ImageView) itemView.findViewById(R.id.cardview_res_image_customer_side);
            _cardView = (CardView)itemView.findViewById(R.id.restaurant_cardview_customer_side);

        }
    }

    @NonNull
    @Override
    public RestaurantOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_restaurants, parent, false);
        ViewHolder vh = new ViewHolder((CardView) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantOptionAdapter.ViewHolder holder, int position) {

        final String tempName = _restaurant.get(position).getRestaurantname();
        final String tempKey = _restaurant.get(position).getRestaurantKey();

        holder._restaurantName.setText(tempName);


        Picasso.get()
                .load(_restaurant.get(position).getRestaurantImage())
                .into(holder._restaurantImage);

        holder._cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Intent i = new Intent(_context, RestaurantFoodMenu.class);
                 i.putExtra("RestaurantKey", tempKey);
                 i.putExtra("RestaurantName", tempName);
                 _context.startActivity(i);
            }
        });



    }

    @Override
    public int getItemCount() {
        return _restaurant.size();
    }
}
