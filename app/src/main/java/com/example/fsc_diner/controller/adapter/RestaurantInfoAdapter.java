package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.FoodMenuManagerSide;
import com.example.fsc_diner.model.RestaurantInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantInfoAdapter extends RecyclerView.Adapter<RestaurantInfoAdapter.ViewHolder> {

    private Context _context;
    private List<RestaurantInfo> _restaurant;

    public RestaurantInfoAdapter(Context _context, List<RestaurantInfo> _restaurant) {
        this._context = _context;
        this._restaurant = _restaurant;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView _restaurantName;
        public ImageView _restaurantImage;
        public CardView _cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _restaurantName= itemView.findViewById(R.id.text_card_view);
            _restaurantImage= itemView.findViewById(R.id.image_card_view);
            _cardView = itemView.findViewById(R.id.restaurant_card_view);

        }
    }

    @NonNull
    @Override
    public RestaurantInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cutom_cardview_manager_side, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantInfoAdapter.ViewHolder holder, int position) {

        final String tempName = _restaurant.get(position).getRestaurantname();
        final String tempKey = _restaurant.get(position).getRestaurantKey();

        holder._restaurantName.setText(tempName);


        Picasso.get()
                .load(_restaurant.get(position).getRestaurantImage())
                .into(holder._restaurantImage);

        holder._cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), FoodMenuManagerSide.class);
                intent.putExtra("RestaurantName", tempName);
                intent.putExtra("RestaurantKey", tempKey);
                v.getContext().startActivity(intent);
            }
        });


        holder._cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                menu.add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        //do something
                        return true;
                    }
                });

                menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        //do something
                        return true;
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return _restaurant.size();
    }
}
