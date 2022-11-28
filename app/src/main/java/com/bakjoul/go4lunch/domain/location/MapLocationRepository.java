package com.bakjoul.go4lunch.domain.location;

import android.location.Location;

import androidx.lifecycle.LiveData;

public interface MapLocationRepository {

    LiveData<Location> getCurrentMapLocationLiveData();

    void setCurrentMapLocation(Location currentMapLocation);
}
