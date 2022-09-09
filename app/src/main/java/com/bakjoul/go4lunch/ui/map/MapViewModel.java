package com.bakjoul.go4lunch.ui.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.LocationRepository;
import com.bakjoul.go4lunch.data.PermissionRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    @NonNull
    private final LocationRepository locationRepository;

    @NonNull
    private final PermissionRepository permissionRepository;

    private LiveData<MapViewState> mapViewState;

    @Inject
    public MapViewModel(@NonNull LocationRepository locationRepository, @NonNull PermissionRepository permissionRepository) {
        this.locationRepository = locationRepository;
        this.permissionRepository = permissionRepository;

        mapViewState = Transformations.switchMap(
            permissionRepository.getLocationPermissionState(), new Function<Boolean, LiveData<MapViewState>>() {
                @Override
                public LiveData<MapViewState> apply(Boolean isLocationPermissionGranted) {

                    if (isLocationPermissionGranted) {
                        mapViewState = Transformations.map(
                            locationRepository.getCurrentLocation(), new Function<Location, MapViewState>() {
                                MapViewState mapViewState = new MapViewState(0, 0);

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
                    return mapViewState;
                }
            }
        );
    }


/*        mapViewState = Transformations.map(
            locationRepository.getCurrentLocation(), new Function<Location, MapViewState>() {
                MapViewState mapViewState = new MapViewState(0, 0);

                @Override
                public MapViewState apply(Location location) {
                    if (location != null) {
                        mapViewState = new MapViewState(location.getLatitude(), location.getLongitude());
                    }
                    return mapViewState;
                }
            }
        );
    }*/

    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewState;
    }
}
