package com.bakjoul.go4lunch.ui.map;

import static com.bakjoul.go4lunch.data.restaurant.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.restaurant.RestaurantRepository.TYPE;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.GpsLocationRepository;
import com.bakjoul.go4lunch.data.repository.GpsModeRepository;
import com.bakjoul.go4lunch.data.repository.MapLocationRepository;
import com.bakjoul.go4lunch.data.restaurant.RestaurantRepository;
import com.bakjoul.go4lunch.data.restaurant.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    private static final String TAG = "MapViewModel";
    private static final double MAP_MINIMUM_DISPLACEMENT = 1000;

    @NonNull
    private final LocationDistanceUtil locationDistanceUtil;

    @NonNull
    private final GpsLocationRepository gpsLocationRepository;

    @NonNull
    private final MapLocationRepository mapLocationRepository;

    private final MutableLiveData<Boolean> isMapReadyMutableLiveData = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

    private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public MapViewModel(
        @NonNull GpsLocationRepository gpsLocationRepository,
        @NonNull GpsModeRepository gpsModeRepository,
        @NonNull RestaurantRepository restaurantRepository,
        @NonNull LocationDistanceUtil locationDistanceUtil,
        @NonNull MapLocationRepository mapLocationRepository
    ) {
        this.gpsLocationRepository = gpsLocationRepository;
        this.locationDistanceUtil = locationDistanceUtil;
        this.mapLocationRepository = mapLocationRepository;

        LiveData<Location> gpsLocationLiveData = gpsLocationRepository.getCurrentLocation();
        LiveData<Location> mapLocationLiveData = mapLocationRepository.getCurrentMapLocation();
        LiveData<Boolean> isUserModeEnabledLiveData = gpsModeRepository.isUserModeEnabledLiveData();

        LiveData<Location> locationLiveData = Transformations.switchMap(
            isUserModeEnabledLiveData,
            isUserModeEnabled -> {
                if (isUserModeEnabled) {
                    return mapLocationRepository.getCurrentMapLocation();
                } else {
                    return gpsLocationRepository.getCurrentLocation();
                }
            }
        );

        LiveData<RestaurantResponseWrapper> responseWrapperLiveData = Transformations.switchMap(
            locationLiveData,
            location -> {
                // TODO BAkjoul viewAction.setValue(bougeTaCameraPelo)
                return getRestaurantsMarkersLiveData(location, restaurantRepository);
            }
        );

        mapViewStateMediatorLiveData.addSource(isMapReadyMutableLiveData, isMapReady ->
            combine(isMapReady, isLocationGpsBasedMutableLiveData.getValue(), gpsLocationLiveData.getValue(), mapLocationLiveData.getValue(), responseWrapperLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(isLocationGpsBasedMutableLiveData, isLocationGpsBased ->
            combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBased, gpsLocationLiveData.getValue(), mapLocationLiveData.getValue(), responseWrapperLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(gpsLocationLiveData, gpsLocation ->
            combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue(), gpsLocation, mapLocationLiveData.getValue(), responseWrapperLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(mapLocationLiveData, mapLocation ->
            combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue(), gpsLocationLiveData.getValue(), mapLocation, responseWrapperLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(responseWrapperLiveData, restaurantMarkers ->
            combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue(), gpsLocationLiveData.getValue(), mapLocationLiveData.getValue(), restaurantMarkers)
        );
    }

    @NonNull
    private LiveData<RestaurantResponseWrapper> getRestaurantsMarkersLiveData(
        Location location,
        @NonNull RestaurantRepository restaurantRepository
    ) {
        return Transformations.switchMap(
            nearbySearchRequestPingMutableLiveData,
            aVoid -> restaurantRepository.getNearbySearchResult(getLocation(location), RANK_BY, TYPE, BuildConfig.MAPS_API_KEY)
        );
    }


    private void combine(
        @Nullable Boolean isMapReady,
        @Nullable Boolean isLocationGpsBased,
        @Nullable Location gpsLocation,
        @Nullable Location mapLocation,
        @Nullable RestaurantResponseWrapper restaurantResponseWrapper
    ) {
        if (isMapReady == null || isLocationGpsBased == null || restaurantResponseWrapper == null) {
            return;
        }

        List<RestaurantMarker> restaurantsMarkers = new ArrayList<>();
        boolean isProgressBarVisible = restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.LOADING;

        if (restaurantResponseWrapper.getNearbySearchResult() != null
            && restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.SUCCESS) {

            isProgressBarVisible = false;

            if (restaurantResponseWrapper.getNearbySearchResult().getResponse() != null) {
                for (RestaurantResponse r : restaurantResponseWrapper.getNearbySearchResult().getResponse().getResults()) {
                    if (r.getBusinessStatus() != null && r.getBusinessStatus().equals("OPERATIONAL")) {
                        restaurantsMarkers.add(
                            new RestaurantMarker(
                                r.getPlaceId(),
                                new LatLng(
                                    r.getGeometry().getLocation().getLat(),
                                    r.getGeometry().getLocation().getLng()
                                ),
                                r.getName(),
                                R.drawable.ic_restaurant_red_marker
                            )
                        );
                    }
                }
            }
        }

        if (restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.IO_ERROR) {
            // TODO Bakjoul ViewAction.snackbar ou ViewState suivant ton besoin
        }

        if (isMapReady) {
            mapViewStateMediatorLiveData.setValue(
                new MapViewState(
                    restaurantsMarkers,
                    isProgressBarVisible
                )
            );
        }
    }

    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewStateMediatorLiveData;
    }

    @NonNull
    private String getLocation(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    public void onMapReady() {
        isMapReadyMutableLiveData.setValue(true);
    }

    public void onRetryButtonClicked() {
        nearbySearchRequestPingMutableLiveData.setValue(true);
    }

    public LiveData<Boolean> getNearbySearchRequestPingMutableLiveData() {
        return nearbySearchRequestPingMutableLiveData;
    }

    public void onCameraMoved(@NonNull LatLng cameraPosition) {
        if (locationDistanceUtil.getDistance(newPosition, oldPosition) > MAP_MINIMUM_DISPLACEMENT) {
            gpsLocationRepository.onCameraMoved();

            Location mapLocation = new Location(LocationManager.GPS_PROVIDER);
            mapLocation.setLatitude(newPosition.latitude);
            mapLocation.setLongitude(newPosition.longitude);
            mapLocationRepository.setCurrentMapLocation(mapLocation);
            Log.d("test", "onCameraMoved: " + locationDistanceUtil.getDistance(newPosition, oldPosition));
        }
    }

    public void onMyLocationButtonClicked() {
        gpsLocationRepository.onMyLocationButtonClicked();
    }
}
