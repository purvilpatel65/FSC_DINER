package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.model.OrderItem;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CurrentOrdersAdapter extends RecyclerView.Adapter<CurrentOrdersAdapter.ViewHolder> {

    private Context _context;
    private List<OrderItem> _items;

    public CurrentOrdersAdapter(Context _context, List<OrderItem> _items) {
        this._context = _context;
        this._items = _items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

       public TextView _orderId;
       public TextView _itemName;
       public TextView _restaurantName;
       public TextView _itemQuantity;
       public StepView _stepView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _orderId = itemView.findViewById(R.id.current_order_orderId);
            _itemName = itemView.findViewById(R.id.current_order_item_name);
            _restaurantName = itemView.findViewById(R.id.current_order_restaurant_name);
            _itemQuantity = itemView.findViewById(R.id.current_order_quantity);
            _stepView = itemView.findViewById(R.id.current_order_step_view);
        }

    }

    @NonNull
    @Override
    public CurrentOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_current_order, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CurrentOrdersAdapter.ViewHolder holder, int position) {

        holder._orderId.setText(Integer.toString(_items.get(position).getOrderID()));
        holder._restaurantName.setText(_items.get(position).getRestaurantName());
        holder._itemName.setText(_items.get(position).getItemName());
        holder._itemQuantity.setText(Integer.toString(_items.get(position).getQuantity()));

        holder._stepView.getState()
                .steps(new ArrayList<String>() {{
            add("Order being placed");
            add("Order being handled");
            add("Order in process");
            add("Order ready to pickup");
        }}).commit();

        holder._stepView.go(holder._stepView.getCurrentStep() + (_items.get(position).getStatus() - (holder._stepView.getCurrentStep()+1)), true);



    }

    @Override
    public int getItemCount() {
        return _items.size();
    }
}
