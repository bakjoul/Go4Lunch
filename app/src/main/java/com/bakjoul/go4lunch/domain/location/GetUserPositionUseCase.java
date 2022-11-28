package com.bakjoul.go4lunch.domain.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import javax.inject.Inject;

public class GetUserPositionUseCase {

    @NonNull
    private final LocationModeRepository locationModeRepository;
    @NonNull
    private final GpsLocationRepository gpsLocationRepository;
    @NonNull
    private final MapLocationRepository mapLocationRepository;

    @Inject
    public GetUserPositionUseCase(
        @NonNull LocationModeRepository locationModeRepository,
        @NonNull GpsLocationRepository gpsLocationRepository,
        @NonNull MapLocationRepository mapLocationRepository
    ) {
        this.locationModeRepository = locationModeRepository;
        this.gpsLocationRepository = gpsLocationRepository;
        this.mapLocationRepository = mapLocationRepository;
    }

    public LiveData<Location> invoke() {
        LiveData<Boolean> isUserModeEnabledLiveData = locationModeRepository.isUserModeEnabledLiveData();

        return Transformations.switchMap(
            isUserModeEnabledLiveData,
            isUserModeEnabled -> {
                if (isUserModeEnabled) {
                    return mapLocationRepository.getCurrentMapLocationLiveData();
                } else {
                    return gpsLocationRepository.getCurrentLocationLiveData();
                }
            }
        );
    }
}
