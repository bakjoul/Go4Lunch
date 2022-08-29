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

    public static RestaurantsFragment newInstance() {
        return new RestaurantsFragment();
    }

    private FragmentRestaurantsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
