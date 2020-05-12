package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.AddIngredientsManagerFragment;
import com.example.fsc_diner.controller.DeleteFoodItemDialog;
import com.example.fsc_diner.controller.EditFoodItemDialog;
import com.example.fsc_diner.model.FoodItemInfo;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
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

        private TextView _itemName;
        private TextView _itemCalorie;
        private TextView _itemPrice;
        private ImageView _itemImage;
        private CardView _cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _itemName= itemView.findViewById(R.id.manager_food_item_name);
            _itemCalorie= itemView.findViewById(R.id.manager_food_item_calorie);
            _itemPrice= itemView.findViewById(R.id.manager_food_item_price);
            _itemImage= itemView.findViewById(R.id.manager_food_item_image);
            _cardView = itemView.findViewById(R.id.manager_food_item_card_view);
        }
    }

    @NonNull
    @Override
    public FoodItemInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_card_view_food_item_manager_side, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull final FoodItemInfoAdapter.ViewHolder holder, int position) {

        final String tempName = _items.get(position).getItemName();
        final String tempKey = _items.get(position).getItemKey();
        final String tempResKey = _items.get(position).getRestaurantKey();

        holder._itemName.setText(tempName);
        holder._itemPrice.setText("$" + new DecimalFormat("0.00").format(_items.get(position).getItemPrice()));
        holder._itemCalorie.setText(Integer.toString(_items.get(position).getItemCalories()));



        Picasso.get()
                .load(_items.get(position).getItemImage())
                .into(holder._itemImage);

        holder._cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("ItemName",tempName);
                args.putString("ItemKey",tempKey);
                args.putString("RestaurantKey",tempResKey);
                args.putString("RestaurantName", _restaurantName);


                AddIngredientsManagerFragment aim = new AddIngredientsManagerFragment();
                aim.setArguments(args);

                FragmentTransaction fragmentTransaction = ((AppCompatActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment_manager, aim);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        holder._cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {

                menu.add("Edit Menu Item").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Bundle args = new Bundle();
                        args.putString("ItemName",tempName);
                        args.putString("ItemKey",tempKey);
                        args.putString("RestaurantKeyCurrent",tempResKey);
                        EditFoodItemDialog dialog = new EditFoodItemDialog();
                        dialog.setArguments(args);
                        dialog.show(((AppCompatActivity)v.getContext()).getSupportFragmentManager(),"EditFood");
                        return true;
                    }
                });

                menu.add("Delete Menu Item").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Bundle args = new Bundle();
                        args.putString("ItemKey",tempKey);
                        args.putString("RestaurantKeyCurrent",tempResKey);
                        DeleteFoodItemDialog dialog = new DeleteFoodItemDialog();
                        dialog.setArguments(args);
                        dialog.show(((AppCompatActivity)v.getContext()).getSupportFragmentManager(),"DeleteFood");
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
