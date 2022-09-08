package com.bakjoul.go4lunch.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends SupportMapFragment {

    @NonNull
    public static MapFragment newInstance() {
        return new MapFragment();
    }

/*    @Inject
    LocationRepository locationRepository;*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                viewModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onChanged(Location location) {
                        if (location != null) {
                            viewModel.animateCamera(location, googleMap, 14);
                            googleMap.setMyLocationEnabled(true);
                        }
                    }
                });
                /*locationRepository.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onChanged(Location location) {
                        if (location != null) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                    ),
                                    14
                                )
                            );
                            googleMap.setMyLocationEnabled(true);
                        }
                    }
                });*/

            }
        });
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
