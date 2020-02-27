package com.example.fsc_diner.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.fsc_diner.R;

public class CheckOutFragment extends Fragment {

    private CheckOutViewModel checkOutViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        checkOutViewModel =
                ViewModelProviders.of(this).get(CheckOutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_check_out, container, false);

        return root;
    }
}