package com.bakjoul.go4lunch.data.location;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bakjoul.go4lunch.domain.location.GpsLocationRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GpsLocationRepositoryImplementation implements GpsLocationRepository {

    private static final String TAG = "GpsLocationRepoImplemen";

    private static final long INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = INTERVAL / 2;
    private static final float SMALLEST_DISPLACEMENT = 20f;

    private final MutableLiveData<Boolean> isLocationPermissionAllowedLiveData = new MutableLiveData<>(false);

    @NonNull
    private final FusedLocationProviderClient fusedLocationProvider;

    private final LocationCallback locationCallback;

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>(null);

    private final LocationRequest locationRequest = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, INTERVAL)
        .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
        .setMinUpdateDistanceMeters(SMALLEST_DISPLACEMENT)
        .build();

    @Inject
    public GpsLocationRepositoryImplementation(
        @NonNull FusedLocationProviderClient fusedLocationProvider) {
        this.fusedLocationProvider = fusedLocationProvider;
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.d(TAG, "onLocationResult() called with: locationResult = [" + locationResult + "]");
                locationMutableLiveData.setValue(locationResult.getLastLocation());
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public LiveData<Location> getCurrentLocationLiveData() {
        return Transformations.switchMap(isLocationPermissionAllowedLiveData, isLocationPermissionAllowed -> {
            Log.d(TAG, "switchMap() called with: isLocationPermissionAllowed = [" + isLocationPermissionAllowed + "]");

            if (isLocationPermissionAllowed) {
                fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            } else {
                fusedLocationProvider.removeLocationUpdates(locationCallback);
            }

            return locationMutableLiveData;
        });
    }

    @Override
    public void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates() called");
        isLocationPermissionAllowedLiveData.setValue(true);
    }

    @Override
    public void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates() called");
        isLocationPermissionAllowedLiveData.setValue(false);
    }
}
