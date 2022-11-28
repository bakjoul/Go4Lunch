package com.bakjoul.go4lunch.domain.location;

import android.location.Location;

import androidx.lifecycle.LiveData;

public interface GpsLocationRepository {

    LiveData<Location> getCurrentLocationLiveData();

    void startLocationUpdates();

    void stopLocationUpdates();
}
