package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.AddRestaurantDialog;
import com.example.fsc_diner.controller.DeleteRestaurantDialog;
import com.example.fsc_diner.controller.EditRestaurantDialog;
import com.example.fsc_diner.controller.FoodMenuManagerFragment;
import com.example.fsc_diner.controller.MainActivity;
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
    public void onBindViewHolder(@NonNull RestaurantInfoAdapter.ViewHolder holder, final int position) {
        final String tempName = _restaurant.get(position).getRestaurantname();
        final String tempKey = _restaurant.get(position).getRestaurantKey();

        holder._restaurantName.setText(tempName);

        Picasso.get()
                .load(_restaurant.get(position).getRestaurantImage())
                .into(holder._restaurantImage);

        holder._cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("RestaurantName",tempName);
                args.putString("RestaurantKey",tempKey);
                FoodMenuManagerFragment fmf = new FoodMenuManagerFragment();
                fmf.setArguments(args);

                FragmentTransaction fragmentTransaction = ((AppCompatActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_manager, fmf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        holder._cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {

                menu.add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        EditRestaurantDialog dialog = new EditRestaurantDialog();
                        Bundle args = new Bundle();
                        args.putString("RestaurantName",tempName);
                        args.putString("RestaurantKey",tempKey);
                        dialog.setArguments(args);
                        dialog.show(((AppCompatActivity)v.getContext()).getSupportFragmentManager(),"EditResDialog");
                        //do something
                        return true;
                    }
                });

                menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        DeleteRestaurantDialog dialog = new DeleteRestaurantDialog();
                        Bundle args = new Bundle();
                        args.putString("RestaurantName",tempName);
                        args.putString("RestaurantKey",tempKey);
                        dialog.setArguments(args);
                        dialog.show(((AppCompatActivity)v.getContext()).getSupportFragmentManager(),"DeleteResDialog");
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
