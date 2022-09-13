package com.bakjoul.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bakjoul.go4lunch.databinding.FragmentNoPermissionBinding;

public class NoPermissionFragment extends Fragment {

    public static NoPermissionFragment newInstance() {
        return new NoPermissionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentNoPermissionBinding binding = FragmentNoPermissionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
