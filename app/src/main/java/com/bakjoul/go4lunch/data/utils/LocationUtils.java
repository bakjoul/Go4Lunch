package com.bakjoul.go4lunch.data.utils;

import android.location.Location;

import androidx.annotation.NonNull;

public class LocationUtils {
    @NonNull
    public static String locationToString(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }
}
