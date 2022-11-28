package com.bakjoul.go4lunch.data.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.location.LocationPermissionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationPermissionRepositoryImplementation implements LocationPermissionRepository {

    private static final String TAG = "LocationPermissRepoImpl";

    private final MutableLiveData<Boolean> isLocationPermissionGranted = new MutableLiveData<>();

    @Inject
    public LocationPermissionRepositoryImplementation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted.setValue(true);
        } else {
            isLocationPermissionGranted.setValue(false);
        }
    }

    @Override
    public LiveData<Boolean> getLocationPermissionLiveData() {
        return isLocationPermissionGranted;
    }

    @Override
    public void setLocationPermission(boolean granted) {
        Log.d(TAG, "setLocationPermission() called with granted = " + granted);
        isLocationPermissionGranted.setValue(granted);
    }
}
