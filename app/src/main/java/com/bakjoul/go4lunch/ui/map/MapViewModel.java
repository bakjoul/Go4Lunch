package com.bakjoul.go4lunch.ui.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.LocationRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    private final LiveData<MapViewState> mapViewState;

    @Inject
    public MapViewModel(@NonNull LocationRepository locationRepository) {

        mapViewState = Transformations.map(
            locationRepository.getCurrentLocation(), new Function<Location, MapViewState>() {
                MapViewState mapViewState;

                @Override
                public MapViewState apply(Location location) {
                    if (location != null) {
                        mapViewState = new MapViewState(location.getLatitude(), location.getLongitude());
                    }
                    return mapViewState;
                }
            }
        );
    }

    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewState;
    }
}
