package com.bakjoul.go4lunch.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PermissionRepository {

    private static final String TAG = "PermissionRepository";

    private final Context context;

    private final MutableLiveData<Boolean> isLocationPermissionGranted = new MutableLiveData<>(false);

    @Inject
    public PermissionRepository(Context context) {
        this.context = context;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted.setValue(true);
        } else {
            isLocationPermissionGranted.setValue(false);
        }
    }

    public LiveData<Boolean> getLocationPermissionLiveData() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted.setValue(true);
        } else {
            isLocationPermissionGranted.setValue(false);
        }
        return isLocationPermissionGranted;
    }

    public void onLocationPermissionGranted() {
        Log.d(TAG, "onLocationPermissionGranted() called");
        isLocationPermissionGranted.setValue(true);
    }

    public void onLocationPermissionDenied() {
        Log.d(TAG, "onLocationPermissionDenied() called");
        isLocationPermissionGranted.setValue(false);
    }
}
