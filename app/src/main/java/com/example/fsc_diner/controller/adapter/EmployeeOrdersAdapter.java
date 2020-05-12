package com.example.fsc_diner.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.OrderItemEmployeeSide;
import com.example.fsc_diner.model.OrderItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EmployeeOrdersAdapter extends RecyclerView.Adapter<EmployeeOrdersAdapter.ViewHolder> {

    private Context _context;
    private List<OrderItem> _items;
    private String _empName;

    public EmployeeOrdersAdapter(Context _context, List<OrderItem> _items, String empName) {
        this._context = _context;
        this._items = _items;
        this._empName = empName;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView _orderId;
        public Button _statusButton;
        public CardView _cardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _orderId = itemView.findViewById(R.id.employee_order_orderId);
            _statusButton = itemView.findViewById(R.id.employee_order_status_button);
            _cardview = itemView.findViewById(R.id.employee_order_cardview);
        }

    }

    @NonNull
    @Override
    public EmployeeOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardview_employee_order, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final EmployeeOrdersAdapter.ViewHolder holder, final int position) {

        holder._orderId.setText(Integer.toString(_items.get(position).getOrderID()));

        if(_items.get(position).getStatus() == 1){
            GradientDrawable bgShape = (GradientDrawable)holder._statusButton.getBackground();
            bgShape.setColor(Color.RED);
        }
        else if(_items.get(position).getStatus() == 2){
            GradientDrawable bgShape = (GradientDrawable)holder._statusButton.getBackground();
            bgShape.setColor(Color.RED);
        }
        else if(_items.get(position).getStatus() == 3){
            GradientDrawable bgShape = (GradientDrawable)holder._statusButton.getBackground();
            bgShape.setColor(Color.GREEN);
        }
        else if(_items.get(position).getStatus() == 4){
            GradientDrawable bgShape = (GradientDrawable)holder._statusButton.getBackground();
            bgShape.setColor(Color.BLUE);
        }

        holder._cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(_items.get(position).getStatus() == 4 || _items.get(position).getStatus() == 3) {
                   // Toast.makeText(_context, "I am status: " + _items.get(position).getStatus(), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(v.getContext(), OrderItemEmployeeSide.class);
                    i.putExtra("EmployeeOrderKey", _items.get(position).getEmployeeOrderItemKey());
                    i.putExtra("RestaurantKey", _items.get(position).getRestaurantKey());
                    i.putExtra("CustomerUid", _items.get(position).getCustometUID());
                    i.putExtra("OrderId", _items.get(position).getOrderID());
                    i.putExtra("OrderStatus", _items.get(position).getStatus());
                    i.putExtra("EmpName", _empName);
                    i.putExtra("HandledBy", _items.get(position).getHandledBy());
                    v.getContext().startActivity(i);
                }
                else{

                    updateOrderStatusOnOrderClick(_items.get(position).getRestaurantKey(), _items.get(position).getEmployeeOrderItemKey(), _items.get(position).getCustometUID(), 3, _empName);
                    //Toast.makeText(_context, "I am status: " + _items.get(position).getStatus(), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(v.getContext(), OrderItemEmployeeSide.class);
                    i.putExtra("EmployeeOrderKey", _items.get(position).getEmployeeOrderItemKey());
                    i.putExtra("RestaurantKey", _items.get(position).getRestaurantKey());
                    i.putExtra("CustomerUid", _items.get(position).getCustometUID());
                    i.putExtra("OrderId", _items.get(position).getOrderID());
                    i.putExtra("OrderStatus", _items.get(position).getStatus());
                    i.putExtra("EmpName", _empName);
                    v.getContext().startActivity(i);

                }
            }
        });

        if(_items.get(position).getStatus() == 4) {

            final String uploadKey = FirebaseDatabase.getInstance().getReference("Users").child(_items.get(position).getCustometUID()).child("History").push().getKey();

            holder._cardview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add("Picked-Up").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            onPickedUpClick(_items.get(position).getEmployeeOrderItemKey()
                                           ,_items.get(position).getRestaurantKey()
                                           ,_items.get(position).getCustometUID());
                            return true;
                        }
                    });

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    private void updateOrderStatusOnOrderClick(String resKey, String empOrderKey, final String customerUid, final int status, final String employeeName){

        FirebaseDatabase.getInstance().getReference("Restaurant").child(resKey).child("Orders").child(empOrderKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    snapshot.getRef().child("status").setValue(status);
                    snapshot.getRef().child("handledBy").setValue(employeeName);

                    FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("CurrentOrders").child(snapshot.getKey()).child("status").setValue(status);
                    FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("CurrentOrders").child(snapshot.getKey()).child("handledBy").setValue(employeeName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void onPickedUpClick(String employeeOrderKey, String resKey, final String customerUid){

        FirebaseDatabase.getInstance().getReference("Restaurant").child(resKey).child("Orders").child(employeeOrderKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                int tempCount = (int)dataSnapshot.getChildrenCount();
                final int[] count = new int[]{1};

                for(final DataSnapshot snapshot: dataSnapshot.getChildren()){

                    final String uploadKey =  FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("History").push().getKey();

                    FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("History").child(uploadKey)
                            .setValue(snapshot.getValue(OrderItem.class))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("CurrentOrders").child(snapshot.child("currentOrderItemKey").getValue(String.class))
                                            .removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    snapshot.getRef().removeValue();

                                                    if(count[0] == (int)dataSnapshot.getChildrenCount())
                                                        Toast.makeText(_context, "Order Successfully picked-up. Thanks!", Toast.LENGTH_SHORT).show();
                                                    else
                                                        count[0] = count[0] + 1;
                                                }
                                            })
                                             .addOnFailureListener(new OnFailureListener() {
                                                 @Override
                                                 public void onFailure(@NonNull Exception e) {
                                                     Toast.makeText(_context, "Something wrong in handling pick-up. Please try again!\n" + e.toString(), Toast.LENGTH_LONG).show();
                                                 }
                                             });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(_context, "Something wrong in handling pick-up. Please try again!\n" + e.toString(), Toast.LENGTH_LONG).show();
                                }
                            })
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     FirebaseDatabase.getInstance().getReference("Users").child(customerUid).child("History").child(uploadKey).child("OrderDate")
                                             .setValue((new SimpleDateFormat("MM/dd/yyyy")).format(Calendar.getInstance().getTime()));
                                 }
                             });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
