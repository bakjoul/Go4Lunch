package com.bakjoul.go4lunch.ui.map;

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

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantMarker;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;
import com.bakjoul.go4lunch.domain.location.GpsLocationRepository;
import com.bakjoul.go4lunch.domain.location.LocationModeRepository;
import com.bakjoul.go4lunch.domain.location.MapLocationRepository;
import com.bakjoul.go4lunch.domain.restaurants.RestaurantRepository;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    @NonNull
    private final MapLocationRepository mapLocationRepository;

    @NonNull
    private final LocationModeRepository locationModeRepository;

    private final MutableLiveData<Boolean> isMapReadyMutableLiveData = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

    private final SingleLiveEvent<LatLng> cameraSingleLiveEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Boolean> isRetryBarVisibleSingleLiveEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Boolean> isUserSearchUnmatchedSingleLiveEvent = new SingleLiveEvent<>();

    private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public MapViewModel(
        @NonNull MapLocationRepository mapLocationRepository,
        @NonNull LocationModeRepository locationModeRepository,
        @NonNull GpsLocationRepository gpsLocationRepository,
        @NonNull RestaurantRepository restaurantRepository,
        @NonNull WorkmateRepository workmateRepository,
        @NonNull AutocompleteRepository autocompleteRepository
    ) {
        this.mapLocationRepository = mapLocationRepository;
        this.locationModeRepository = locationModeRepository;

        LiveData<Location> gpsLocationLiveData = gpsLocationRepository.getCurrentLocationLiveData();
        LiveData<Location> mapLocationLiveData = mapLocationRepository.getCurrentMapLocationLiveData();
        LiveData<Boolean> isUserModeEnabledLiveData = Transformations.distinctUntilChanged(
            locationModeRepository.isUserModeEnabledLiveData()
        );

        cameraSingleLiveEvent.addSource(gpsLocationLiveData, location -> {
            if (location != null) {
                cameraSingleLiveEvent.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });

        // TODO FIX DOUBLE CALL WHEN SWITCHING TO USER MODE AFTER HAVING SWITCHED BACK TO GPS
        LiveData<RestaurantResponseWrapper> responseWrapperLiveData = Transformations.switchMap(
            isUserModeEnabledLiveData,
            isUserModeEnabled -> {
                if (isUserModeEnabled) {
                    return Transformations.switchMap(
                        mapLocationLiveData,
                        location -> getRestaurantResponseWrapperLiveData(restaurantRepository, true, location)
                    );
                } else {
                    return Transformations.switchMap(
                        gpsLocationLiveData,
                        location -> getRestaurantResponseWrapperLiveData(restaurantRepository, false, location)
                    );
                }
            }
        );

        LiveData<Collection<String>> chosenRestaurantsLiveData = workmateRepository.getWorkmatesChosenRestaurantsLiveData();
        LiveData<String> userSearchLiveData = autocompleteRepository.getUserSearchLiveData();

        mapViewStateMediatorLiveData.addSource(isMapReadyMutableLiveData, isMapReady ->
            combine(isMapReady, responseWrapperLiveData.getValue(), chosenRestaurantsLiveData.getValue(), userSearchLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(responseWrapperLiveData, responseWrapper ->
            combine(isMapReadyMutableLiveData.getValue(), responseWrapper, chosenRestaurantsLiveData.getValue(), userSearchLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(chosenRestaurantsLiveData, chosenRestaurants ->
            combine(isMapReadyMutableLiveData.getValue(), responseWrapperLiveData.getValue(), chosenRestaurants, userSearchLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(userSearchLiveData, userSearch ->
            combine(isMapReadyMutableLiveData.getValue(), responseWrapperLiveData.getValue(), chosenRestaurantsLiveData.getValue(), userSearch)
        );
    }

    @NonNull
    private LiveData<RestaurantResponseWrapper> getRestaurantResponseWrapperLiveData(
        @NonNull RestaurantRepository restaurantRepository,
        Boolean isUserModeEnabled,
        Location location
    ) {
        if (location == null) {
            return new MutableLiveData<>(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.LOCATION_NULL));
        }
        if (!isUserModeEnabled) {
            cameraSingleLiveEvent.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
            mapLocationRepository.setCurrentMapLocation(location);
        }
        return Transformations.switchMap(
            nearbySearchRequestPingMutableLiveData,
            aVoid -> {
                if (isUserModeEnabled) {
                    Log.d("test", "USERMODE API CALL");
                } else {
                    Log.d("test", "GPS API CALL");
                }
                return restaurantRepository.getNearbyRestaurants(location);
            }
        );
    }

    private void combine(
        @Nullable Boolean isMapReady,
        @Nullable RestaurantResponseWrapper restaurantResponseWrapper,
        @Nullable Collection<String> chosenRestaurants,
        @Nullable String userSearch) {
        if (isMapReady == null || restaurantResponseWrapper == null || chosenRestaurants == null) {
            return;
        }

        List<RestaurantMarker> restaurantsMarkers = new ArrayList<>();
        boolean isProgressBarVisible = restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.LOADING;
        isRetryBarVisibleSingleLiveEvent.setValue(false);
        isUserSearchUnmatchedSingleLiveEvent.setValue(false);

        if (restaurantResponseWrapper.getNearbySearchResponse() != null
            && restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.SUCCESS) {

            isProgressBarVisible = false;

            map(restaurantResponseWrapper, restaurantsMarkers, chosenRestaurants, userSearch);
        }

        if (restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.IO_ERROR
            || restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.CRITICAL_ERROR) {
            isRetryBarVisibleSingleLiveEvent.setValue(true);
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

    private void map(
        @NonNull RestaurantResponseWrapper restaurantResponseWrapper,
        @NonNull List<RestaurantMarker> restaurantsMarkers,
        @NonNull Collection<String> chosenRestaurants,
        @Nullable String userSearch) {
        if (restaurantResponseWrapper.getNearbySearchResponse() != null) {
            boolean isUserSearchMatched = false;
            for (RestaurantResponse response : restaurantResponseWrapper.getNearbySearchResponse().getResults()) {
                if (response.getBusinessStatus() != null && response.getBusinessStatus().equals("OPERATIONAL")) {

                    // By default, non-chosen restaurant pin is red
                    int drawableRes = R.drawable.ic_restaurant_red_marker;
                    // If chosen restaurant, pin is green
                    if (chosenRestaurants.contains(response.getPlaceId())) {
                        drawableRes = R.drawable.ic_restaurant_green_marker;
                    }

                    if (userSearch != null) {
                        // Highlight searched restaurants
/*                        if (!chosenRestaurants.contains(response.getPlaceId()) && response.getName().contains(userSearch)) {
                            drawableRes = R.drawable.ic_restaurant_searched_red_marker;
                        } else if (chosenRestaurants.contains(response.getPlaceId()) && response.getName().contains(userSearch)) {
                            drawableRes = R.drawable.ic_restaurant_searched_green_marker;
                        }*/

                        if (response.getName().contains(userSearch)) {
                            restaurantsMarkers.add(
                                new RestaurantMarker(
                                    response.getPlaceId(),
                                    new LatLng(
                                        response.getGeometry().getLocation().getLat(),
                                        response.getGeometry().getLocation().getLng()
                                    ),
                                    response.getName(),
                                    drawableRes
                                )
                            );
                        }

                        if (response.getName().contains(userSearch) && !isUserSearchMatched) {
                            isUserSearchMatched = true;
                        }

                    } else {
                        restaurantsMarkers.add(
                            new RestaurantMarker(
                                response.getPlaceId(),
                                new LatLng(
                                    response.getGeometry().getLocation().getLat(),
                                    response.getGeometry().getLocation().getLng()
                                ),
                                response.getName(),
                                drawableRes
                            )
                        );
                    }

                }
            }

            if (userSearch != null && !isUserSearchMatched) {
                isUserSearchUnmatchedSingleLiveEvent.setValue(true);
            }
        }
    }

    @NonNull
    private Location latLngToLocation(@NonNull LatLng latLng) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    public SingleLiveEvent<LatLng> getCameraSingleLiveEvent() {
        return cameraSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getIsRetryBarVisibleSingleLiveEvent() {
        return isRetryBarVisibleSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getIsUserSearchUnmatchedSingleLiveEvent() {
        return isUserSearchUnmatchedSingleLiveEvent;
    }

    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewStateMediatorLiveData;
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

    public void onCameraMovedByUser() {
        locationModeRepository.setUserModeEnabled(true);
    }

    public void onCameraMoved(@NonNull LatLng cameraPosition) {
        // Updates current map location
        mapLocationRepository.setCurrentMapLocation(latLngToLocation(cameraPosition));
    }

    public void onMyLocationButtonClicked() {
        locationModeRepository.setUserModeEnabled(false);
    }
}
