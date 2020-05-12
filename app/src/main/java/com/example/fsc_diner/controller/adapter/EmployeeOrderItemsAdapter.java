package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.model.OrderItem;

import java.util.HashMap;
import java.util.List;

public class EmployeeOrderItemsAdapter extends RecyclerView.Adapter<EmployeeOrderItemsAdapter.ViewHolder> {

    public interface OnItemCheckListener {
        void onItemCheck(OrderItem item);
        void onItemUncheck(OrderItem item);
    }

    private Context _context;
    private List<OrderItem> _items;
    private boolean _isVisitor;

    @NonNull
    private OnItemCheckListener _onItemCheckListener;


    public EmployeeOrderItemsAdapter(Context _context, List<OrderItem> _items, boolean isVisitor, @NonNull OnItemCheckListener onItemCheckListener) {
        this._context = _context;
        this._items = _items;
        this._isVisitor = isVisitor;
        this._onItemCheckListener = onItemCheckListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView _itemName;
        public TextView _itemQuantity;
        public TextView _ingredientList;
        public CheckBox _checkBox;
        View _itemView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _itemView = itemView;
            _itemName = itemView.findViewById(R.id.employee_order_items_cardview_title_text);
            _itemQuantity = itemView.findViewById(R.id.employee_order_items_cardview_qnt);
            _ingredientList = itemView.findViewById(R.id.employee_order_items_cardview_ingredient_list);

            _checkBox = itemView.findViewById(R.id.employee_order_items_cardview_checkbox);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            _checkBox.setOnClickListener(onClickListener);
        }
    }

    @NonNull
    @Override
    public EmployeeOrderItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_employee_order_items, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final EmployeeOrderItemsAdapter.ViewHolder holder, final int position) {

        holder._itemName.setText(_items.get(position).getItemName());
        holder._itemQuantity.setText(Integer.toString(_items.get(position).getQuantity()));



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

        if(_items.get(position).getStatus() == 4 || _isVisitor==true){

            holder._checkBox.setVisibility(View.GONE);
        }
        else {

            holder._checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder._checkBox.isChecked()) {
                        _onItemCheckListener.onItemCheck(_items.get(position));
                    } else {
                        _onItemCheckListener.onItemUncheck(_items.get(position));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }
}
