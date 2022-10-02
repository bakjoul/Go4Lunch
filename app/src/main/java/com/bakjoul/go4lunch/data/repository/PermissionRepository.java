package com.bakjoul.go4lunch.data.repository;

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

   private final MutableLiveData<Boolean> isLocationPermissionGranted = new MutableLiveData<>();

   @Inject
   public PermissionRepository(Context context) {
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
         isLocationPermissionGranted.setValue(true);
      } else {
         isLocationPermissionGranted.setValue(false);
      }
   }

   public LiveData<Boolean> getLocationPermissionLiveData() {
      return isLocationPermissionGranted;
   }

   public void setLocationPermission(boolean granted) {
      Log.d(TAG, "setLocationPermission() called with granted = " + granted);
      isLocationPermissionGranted.setValue(granted);
   }
}
