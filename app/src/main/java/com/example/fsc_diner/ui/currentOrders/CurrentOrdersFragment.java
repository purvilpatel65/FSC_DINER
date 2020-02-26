package com.example.fsc_diner.ui.currentOrders;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fsc_diner.R;

public class CurrentOrdersFragment extends Fragment {

    private CurrentOrdersViewModel currentOrderViewModel;

    public static CurrentOrdersFragment newInstance() {
        return new CurrentOrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        currentOrderViewModel = ViewModelProviders.of(this).get(CurrentOrdersViewModel.class);


        View root = inflater.inflate(R.layout.fragment_current_orders, container, false);


        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}
