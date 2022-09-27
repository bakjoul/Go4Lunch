package com.bakjoul.go4lunch.data.repository;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationRepository {

    private static final String TAG = "LocationRepository";

    private static final long INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = INTERVAL / 2;
    private static final float SMALLEST_DISPLACEMENT = 20f;

    private final MutableLiveData<Boolean> isLocationPermissionAllowedLiveData = new MutableLiveData<>(false);

    @NonNull
    private final FusedLocationProviderClient fusedLocationProvider;

    private final LocationRequest locationRequest = LocationRequest.create()
        .setInterval(INTERVAL)
        .setFastestInterval(FASTEST_INTERVAL)
        .setPriority(PRIORITY_HIGH_ACCURACY)
        .setSmallestDisplacement(SMALLEST_DISPLACEMENT);

    @Inject
    public LocationRepository(@NonNull FusedLocationProviderClient fusedLocationProvider) {
        this.fusedLocationProvider = fusedLocationProvider;
    }

    public LiveData<Location> getCurrentLocation() {
        return Transformations.switchMap(isLocationPermissionAllowedLiveData, new Function<Boolean, LiveData<Location>>() {
            @SuppressLint("MissingPermission")
            @Override
            public LiveData<Location> apply(Boolean isLocationPermissionAllowed) {
                Log.d(TAG, "switchMap() called with: isLocationPermissionAllowed = [" + isLocationPermissionAllowed + "]");
                MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

                if (isLocationPermissionAllowed) {
                    final LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            Log.d(TAG, "onLocationResult() called with: locationResult = [" + locationResult + "]");
                            locationMutableLiveData.setValue(locationResult.getLastLocation());
                        }
                    };

                    fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                } else {
                    locationMutableLiveData.setValue(null);
                }

                return locationMutableLiveData;
            }
        });
    }

    public void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates() called");
        isLocationPermissionAllowedLiveData.setValue(true);
    }

    public void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates() called");
        isLocationPermissionAllowedLiveData.setValue(false);
    }
}
