package com.bakjoul.go4lunch.domain.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class IsLocationPermissionGrantedUseCase {

    @NonNull
    private final Context context;

    @Inject
    public IsLocationPermissionGrantedUseCase(@NonNull @ApplicationContext Context context) {
        this.context = context;
    }

    public Boolean invoke() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
