package com.bakjoul.go4lunch.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends SupportMapFragment {

    @NonNull
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        viewModel.getMapViewStateLiveData().observe(getViewLifecycleOwner(), mapViewState ->
            getMapAsync(googleMap -> {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(
                            mapViewState.getLatitude(),
                            mapViewState.getLongitude()
                        ),
                        14
                    )
                );
                googleMap.setMyLocationEnabled(true);
            })
        );
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(requireContext())
                    .setTitle("Localisation requise")
                    .setMessage("Pour continuer, activez la localisation de l'appareil.")
                    .setPositiveButton("OK", (dialogInterface, i) -> requestLocationPermission())
                    .create()
                    .show();

            } else {
                requestLocationPermission();
            }
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }


}
