package com.bakjoul.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bakjoul.go4lunch.databinding.FragmentMapViewBinding;

public class MapViewFragment extends Fragment {

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMapViewBinding binding = FragmentMapViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}