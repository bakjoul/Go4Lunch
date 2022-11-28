package com.bakjoul.go4lunch.data.location;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.location.MapLocationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MapLocationRepositoryImplementation implements MapLocationRepository {

    private final MutableLiveData<Location> mapLocationMutableLiveData = new MutableLiveData<>();

    @Inject
    public MapLocationRepositoryImplementation() {
    }

    @Override
    public LiveData<Location> getCurrentMapLocationLiveData() {
        return mapLocationMutableLiveData;
    }

    @Override
    public void setCurrentMapLocation(Location currentMapLocation) {
        mapLocationMutableLiveData.setValue(currentMapLocation);
    }
}
