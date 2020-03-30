package com.example.fsc_diner.controller.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.AddIngredientsManagerSide;
import com.example.fsc_diner.controller.FoodMenuManagerSide;
import com.example.fsc_diner.model.FoodItemInfo;
import com.example.fsc_diner.model.RestaurantInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodItemInfoAdapter extends RecyclerView.Adapter<FoodItemInfoAdapter.ViewHolder> {

    private Context _context;
    private List<FoodItemInfo> _items;
    private String _restaurantName;

    public FoodItemInfoAdapter(Context _context, List<FoodItemInfo> _items, String _restaurantName) {
        this._context = _context;
        this._items = _items;
        this._restaurantName = _restaurantName;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView _itemName;
        public TextView _itemCalorie;
        public TextView _itemPrice;
        public ImageView _itemImage;
        public CardView _cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _itemName= (TextView) itemView.findViewById(R.id.manager_food_item_name);
            _itemCalorie= (TextView) itemView.findViewById(R.id.manager_food_item_calorie);
            _itemPrice= (TextView) itemView.findViewById(R.id.manager_food_item_price);
            _itemImage= (ImageView) itemView.findViewById(R.id.manager_food_item_image);
            _cardView = (CardView)itemView.findViewById(R.id.manager_food_item_card_view);

        }

    }

    @NonNull
    @Override
    public FoodItemInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_card_view_food_item_manager_side, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull final FoodItemInfoAdapter.ViewHolder holder, int position) {

        final String tempName = _items.get(position).getItemName();
        final String tempKey = _items.get(position).getItemKey();
        final String tempResKey = _items.get(position).getRestaurantKey();

        holder._itemName.setText(tempName);
        holder._itemPrice.setText("$" + _items.get(position).getItemPrice());
        holder._itemCalorie.setText(Integer.toString(_items.get(position).getItemCalories()));



        Picasso.get()
                .load(_items.get(position).getItemImage())
                .into(holder._itemImage);

        holder._cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(_context, AddIngredientsManagerSide.class);
                intent.putExtra("ItemName", tempName);
                intent.putExtra("ItemKey", tempKey);
                intent.putExtra("RestaurantName", _restaurantName);
                intent.putExtra("RestaurantKey", tempResKey);
                _context.startActivity(intent);
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
        return _items.size();
    }

}
