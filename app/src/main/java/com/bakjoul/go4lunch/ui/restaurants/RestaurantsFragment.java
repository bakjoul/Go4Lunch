package com.bakjoul.go4lunch.ui.restaurants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bakjoul.go4lunch.databinding.FragmentRestaurantsBinding;

public class RestaurantsFragment extends Fragment {

    private static final String KEY_CLICKED = "KEY_CLICKED";

    public static RestaurantsFragment newInstance() {
        return new RestaurantsFragment();
    }

    private FragmentRestaurantsBinding binding;

    private int clicked = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            clicked = savedInstanceState.getInt(KEY_CLICKED);
        }

        binding.listButton.setText("" + clicked);
        binding.listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked++;
                binding.listButton.setText("" + clicked);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_CLICKED, clicked);
    }
}