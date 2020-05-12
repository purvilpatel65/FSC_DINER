package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.FoodItemDetails;
import com.example.fsc_diner.model.FoodItemInfo;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
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

        private ImageView _foodImage;
        private TextView _foodName;
        private RatingBar _foodRating;
        private TextView _foodCalories;
        private ImageButton _addBtn;
        private Button _priceBtn;
        private CardView _cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _foodImage = itemView.findViewById(R.id.food_pic);
            _foodName = itemView.findViewById(R.id.food_name);
            _foodRating = itemView.findViewById(R.id.food_rating);
            _foodCalories = itemView.findViewById(R.id.food_calorie);
            _addBtn = itemView.findViewById(R.id.add_button);
            _priceBtn = itemView.findViewById(R.id.price_button);
            _cardView = itemView.findViewById(R.id.cv);

        }
    }

    @NonNull
    @Override
    public FoodItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_food_menu, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemsAdapter.ViewHolder holder, final int position) {
        final String tempName= _items.get(position).getItemName();
        final float tempRating= Float.valueOf(_items.get(position).getItemRating());
        final String tempCalories= Integer.toString(_items.get(position).getItemCalories());
        //final String tempPrice= Double.toString(_items.get(position).getItemPrice());
        final String tempPrice= new DecimalFormat("0.00").format(_items.get(position).getItemPrice());
        final String tempKey= _items.get(position).getItemKey();
        final String tempResKey= _items.get(position).getRestaurantKey();

        holder._foodName.setText(tempName);
        holder._foodCalories.setText(tempCalories);
        holder._foodRating.setRating(tempRating);
        holder._foodName.setText(tempName);
        holder._priceBtn.setText("$" + tempPrice);


        Picasso.get()
                .load(_items.get(position).getItemImage()).fit().centerCrop()
                .into(holder._foodImage);


        holder._addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        holder._cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), FoodItemDetails.class);
                i.putExtra("FoodItemName", tempName);
                i.putExtra("FoodItemKey", tempKey);
                i.putExtra("RestaurantKey", tempResKey);
                i.putExtra("RestaurantName", _resName);
                i.putExtra("ItemImage", _items.get(position).getItemImage());
                v.getContext().startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return _items.size();
    }
}
