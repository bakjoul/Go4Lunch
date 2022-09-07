package com.bakjoul.go4lunch.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.FragmentMapViewBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class MapFragment extends Fragment {

    private static final long INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = INTERVAL / 2;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(),
        new ActivityResultCallback<Boolean>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                } else {
                    Log.d("test", "onActivityResult: nonono");
                }
            }
        });

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProvider;
    private final LocationRequest locationRequest =
        LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(FASTEST_INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(5f);
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            currentLocation = locationResult.getLastLocation();
            Log.d("test", "onLocationResult: " + currentLocation);
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapFragment.this::onMapReady);
        }
    };
    private GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMapViewBinding binding = FragmentMapViewBinding.inflate(inflater, container, false);

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext());
        //checkLocationPermission();

        return binding.getRoot();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider.removeLocationUpdates(locationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void onMapReady(@NonNull GoogleMap map) {
        this.map = map;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        map.setMyLocationEnabled(true);
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
