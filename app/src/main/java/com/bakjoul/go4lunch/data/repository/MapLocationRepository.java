package com.bakjoul.go4lunch.data.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MapLocationRepository {

   private final MutableLiveData<Location> mapLocationMutableLiveData = new MutableLiveData<>();

   @Inject
   public MapLocationRepository() {
   }

   public void setCurrentMapLocation(Location currentMapLocation) {
      mapLocationMutableLiveData.setValue(currentMapLocation);
   }

   public LiveData<Location> getCurrentMapLocation() {
      return mapLocationMutableLiveData;
   }
}
