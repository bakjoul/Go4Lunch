package com.bakjoul.go4lunch.ui.map;

import android.location.Location;
import android.location.LocationManager;

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
import com.bakjoul.go4lunch.domain.location.GetUserPositionUseCase;
import com.bakjoul.go4lunch.domain.location.LocationModeRepository;
import com.bakjoul.go4lunch.domain.location.MapLocationRepository;
import com.bakjoul.go4lunch.domain.restaurants.RestaurantRepository;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    private static final double MAP_MINIMUM_DISPLACEMENT = 1000;

    @NonNull
    private final MapLocationRepository mapLocationRepository;

    @NonNull
    private final LocationModeRepository locationModeRepository;

    @NonNull
    private final LocationDistanceUtil locationDistanceUtil;

    private final MutableLiveData<Boolean> isMapReadyMutableLiveData = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

    private final SingleLiveEvent<LatLng> cameraSingleLiveEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Boolean> isRetryBarVisibleSingleLiveEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Boolean> isUserSearchUnmatchedSingleLiveEvent = new SingleLiveEvent<>();

    private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

    private LatLng currentLocation = null;

    private LatLng lastLocation = null;

    @Inject
    public MapViewModel(
        @NonNull GetUserPositionUseCase getUserPositionUseCase,
        @NonNull MapLocationRepository mapLocationRepository,
        @NonNull LocationModeRepository locationModeRepository,
        @NonNull RestaurantRepository restaurantRepository,
        @NonNull WorkmateRepository workmateRepository,
        @NonNull AutocompleteRepository autocompleteRepository,
        @NonNull LocationDistanceUtil locationDistanceUtil
    ) {
        this.mapLocationRepository = mapLocationRepository;
        this.locationModeRepository = locationModeRepository;
        this.locationDistanceUtil = locationDistanceUtil;

        LiveData<Location> locationLiveData = getUserPositionUseCase.invoke();

        cameraSingleLiveEvent.addSource(locationLiveData, location -> {
            if (location != null) {
                cameraSingleLiveEvent.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });

        LiveData<RestaurantResponseWrapper> responseWrapperLiveData = Transformations.switchMap(
            locationLiveData,
            location -> {
                if (location == null) {
                    return new MutableLiveData<>(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.LOCATION_NULL));
                }
                cameraSingleLiveEvent.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                return Transformations.switchMap(
                    nearbySearchRequestPingMutableLiveData,
                    aVoid -> restaurantRepository.getNearbyRestaurants(location)
                );
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

            // Updates last known location
            lastLocation = currentLocation;
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
                        if (!chosenRestaurants.contains(response.getPlaceId()) && response.getName().contains(userSearch)) {
                            drawableRes = R.drawable.ic_restaurant_searched_red_marker;
                        } else if (chosenRestaurants.contains(response.getPlaceId()) && response.getName().contains(userSearch)) {
                            drawableRes = R.drawable.ic_restaurant_searched_green_marker;
                        }

                        if (response.getName().contains(userSearch) && !isUserSearchMatched) {
                            isUserSearchMatched = true;
                        }
                    }

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

            if (userSearch != null && !isUserSearchMatched) {
                isUserSearchUnmatchedSingleLiveEvent.setValue(true);
            }
        }
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
        if (locationModeRepository.isUserModeEnabled()) {
            // If no known last location or if distance between new camera position and last location greater than given value
            if (lastLocation == null || locationDistanceUtil.getDistance(cameraPosition, lastLocation) > MAP_MINIMUM_DISPLACEMENT) {
                // Updates current map location
                Location mapLocation = new Location(LocationManager.GPS_PROVIDER);
                mapLocation.setLatitude(cameraPosition.latitude);
                mapLocation.setLongitude(cameraPosition.longitude);
                mapLocationRepository.setCurrentMapLocation(mapLocation);

                // Updates last location to current camera position
                lastLocation = cameraPosition;
            }
        }
    }

    public void onMyLocationButtonClicked() {
        locationModeRepository.setUserModeEnabled(false);
    }
}
