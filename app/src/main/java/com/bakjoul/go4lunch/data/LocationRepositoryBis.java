package com.bakjoul.go4lunch.data;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class LocationRepositoryBis {

    private static final long INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = INTERVAL / 2;
    private static final float SMALLEST_DISPLACEMENT = 5f;

    private final FusedLocationProviderClient fusedLocationProvider;

    private final LocationRequest locationRequest = LocationRequest.create()
        .setInterval(INTERVAL)
        .setFastestInterval(FASTEST_INTERVAL)
        .setPriority(PRIORITY_HIGH_ACCURACY)
        .setSmallestDisplacement(SMALLEST_DISPLACEMENT);

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            locationMutableLiveData.setValue(locationResult.getLastLocation());
        }
    };

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    public LocationRepositoryBis(FusedLocationProviderClient fusedLocationProvider) {
        this.fusedLocationProvider = fusedLocationProvider;
    }

    public LiveData<Location> getCurrentLocationLiveData() {
        return locationMutableLiveData;
    }

    @SuppressLint("MissingPermission")
    public void startLocationSearch() {
        fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationSearch() {
        fusedLocationProvider.removeLocationUpdates(locationCallback);
        locationMutableLiveData.setValue(null);
    }
}
