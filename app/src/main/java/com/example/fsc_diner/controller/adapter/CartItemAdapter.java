package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.example.fsc_diner.R;
import com.example.fsc_diner.model.CartItem;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private Context _context;
    private List<CartItem> _items;

    public CartItemAdapter(Context _context, List<CartItem> _items) {
        this._context = _context;
        this._items = _items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView _cardView;
        public RelativeLayout _expandableView;
        public TextView _title;
        public TextView _quantity;
        public TextView _remove;
        public TextView _ingredientList;
        public ImageButton _dropButton;
        public ImageButton _upButton;
        public Button _priceButton;
        public CircularImageView _imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _cardView = itemView.findViewById(R.id.cart_item_cardview);
            _expandableView = itemView.findViewById(R.id.expandable_relative_layout);
            _title = itemView.findViewById(R.id.cart_item_title_text);
            _ingredientList = itemView.findViewById(R.id.cart_item_ingredient_list);
            _quantity = itemView.findViewById(R.id.cart_item_qnt);
            _remove = itemView.findViewById(R.id.cart_item_remove_text);
            _dropButton = itemView.findViewById(R.id.cart_item_expandable_arraow);
            _upButton = itemView.findViewById(R.id.cart_item_expandable_arrow_up);
            _priceButton = itemView.findViewById(R.id.cart_item_price_button);
            _imageView = itemView.findViewById(R.id.cart_item_food_pic);

        }

    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart_item, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartItemAdapter.ViewHolder holder, int position) {


        holder._title.setText(_items.get(position).getItemName());
        holder._quantity.setText(Integer.toString(_items.get(position).getQuantity()));
        holder._priceButton.setText("$" + new DecimalFormat("0.00").format(_items.get(position).getTotalPrice()));

        Picasso.get()
                .load(_items.get(position).getImage())
                .into(holder._imageView);

        List<HashMap<String, List<String>>> tempIngredients = _items.get(position).getIngredients();
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String  finalIngredientsList = "";

        for(HashMap<String, List<String>> eachIngredient : tempIngredients){

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

        holder._ingredientList.setText(finalIngredientsList);

        holder._dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    holder._dropButton.setVisibility(View.INVISIBLE);
                    TransitionManager.beginDelayedTransition(holder._cardView, new AutoTransition().setDuration(1000));
                    holder._expandableView.setVisibility(View.VISIBLE);

            }
        });

        holder._upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder._dropButton.setVisibility(View.VISIBLE);
                TransitionManager.beginDelayedTransition(holder._cardView, new AutoTransition().setDuration(1000));
                holder._expandableView.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return _items.size();
    }
}
