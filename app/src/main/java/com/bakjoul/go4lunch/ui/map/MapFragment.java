package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends SupportMapFragment {

    private static final String TAG = "MapFragment";

    @NonNull
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        viewModel.getMapViewStateLiveData().observe(getViewLifecycleOwner(), mapViewState -> {
                if (mapViewState != null) {
                    MapFragment.this.getMapAsync(googleMap -> {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(
                                    mapViewState.getLatitude(),
                                    mapViewState.getLongitude()
                                ),
                                14
                            )
                        );
                        googleMap.setMyLocationEnabled(true);
                    });
                } else {
                    Log.d(TAG, "Location permission is not allowed. Map will not update.");
                }
            }
        );
    }
}
