package com.bakjoul.go4lunch.data;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PermissionRepository {

    private final MutableLiveData<Boolean> isLocationPermissionGranted = new MutableLiveData<>(false);

    @Inject
    public PermissionRepository() {
    }

    public LiveData<Boolean> getLocationPermissionState() {
        return isLocationPermissionGranted;
    }

    public void checkLocationPermission(Activity activity, Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showRationale(activity, context);
        }
    }

    public void showRationale(Activity activity, Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(context)
                .setTitle("Localisation requise")
                .setMessage("Pour continuer, activez la localisation de l'appareil.")
                .setPositiveButton("OK", (dialogInterface, i) -> requestLocationPermission(activity))
                .create()
                .show();
        }
    }

    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void onLocationPermissionGranted() {
        isLocationPermissionGranted.setValue(true);
    }

    public void onLocationPermissionDenied() {
        isLocationPermissionGranted.setValue(false);
    }
}
