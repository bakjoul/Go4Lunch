package com.bakjoul.go4lunch.ui.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.LocationRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;

    @Inject
    public MapViewModel(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LiveData<Location> getCurrentLocation() {
        return locationRepository.getCurrentLocation();
    }

    public void animateCamera(@NonNull Location location, @NonNull GoogleMap googleMap, float zoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
                ),
                zoom
            )
        );
    }
}
