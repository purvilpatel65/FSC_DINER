package com.example.fsc_diner.ui.ordersHistory;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fsc_diner.R;

public class ordersHistory extends Fragment {

    private OrdersHistoryViewModel ordersHistoryViewModel;

    public static ordersHistory newInstance() {
        return new ordersHistory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ordersHistoryViewModel = ViewModelProviders.of(this).get(OrdersHistoryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_orders_history, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}
