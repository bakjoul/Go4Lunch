package com.bakjoul.go4lunch.ui.map;

import static com.bakjoul.go4lunch.data.restaurants.RestaurantRepositoryImplementation.RANK_BY;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;
import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;
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
    private static final String RADIUS = "3000";
    private static final String TYPE = "restaurant";

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
                    aVoid -> restaurantRepository.getNearbyRestaurants(location, RANK_BY, TYPE, BuildConfig.MAPS_API_KEY)
                );
            }
        );

        LiveData<Collection<String>> chosenRestaurantsLiveData = workmateRepository.getWorkmatesChosenRestaurantsLiveData();

        LiveData<List<PredictionResponse>> predictionsLiveData = Transformations.switchMap(
            locationLiveData,
            new Function<Location, LiveData<List<PredictionResponse>>>() {
                @Override
                public LiveData<List<PredictionResponse>> apply(Location location) {
                    if (location == null) {
                        return new MutableLiveData<>(null);
                    }
                    return Transformations.switchMap(
                        autocompleteRepository.getUserQuery(),
                        new Function<String, LiveData<List<PredictionResponse>>>() {
                            @Override
                            public LiveData<List<PredictionResponse>> apply(String userInput) {
                                Log.d("test", "apply: " + userInput);
                                if (userInput != null && userInput.length() >= 3) {
                                    return Transformations.switchMap(
                                        autocompleteRepository.getAutocompleteResponse(userInput, location, RADIUS, TYPE, BuildConfig.MAPS_API_KEY),
                                        new Function<AutocompleteResponse, LiveData<List<PredictionResponse>>>() {
                                            @Override
                                            public LiveData<List<PredictionResponse>> apply(AutocompleteResponse response) {
                                                if (response != null) {
                                                    return new MutableLiveData<>(response.getPredictions());
                                                }
                                                return new MutableLiveData<>(new ArrayList<>());
                                            }
                                        }
                                    );
                                }
                                return new MutableLiveData<>(new ArrayList<>());
                            }
                        }
                    );
                }
            }
        );

        mapViewStateMediatorLiveData.addSource(isMapReadyMutableLiveData, isMapReady ->
            combine(isMapReady, responseWrapperLiveData.getValue(), chosenRestaurantsLiveData.getValue(), predictionsLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(responseWrapperLiveData, responseWrapper ->
            combine(isMapReadyMutableLiveData.getValue(), responseWrapper, chosenRestaurantsLiveData.getValue(), predictionsLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(chosenRestaurantsLiveData, chosenRestaurants ->
            combine(isMapReadyMutableLiveData.getValue(), responseWrapperLiveData.getValue(), chosenRestaurants, predictionsLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(predictionsLiveData, suggestions ->
            combine(isMapReadyMutableLiveData.getValue(), responseWrapperLiveData.getValue(), chosenRestaurantsLiveData.getValue(), suggestions)
        );
    }

    private void combine(
        @Nullable Boolean isMapReady,
        @Nullable RestaurantResponseWrapper restaurantResponseWrapper,
        @Nullable Collection<String> chosenRestaurants,
        @Nullable List<PredictionResponse> predictions) {
        if (isMapReady == null || restaurantResponseWrapper == null || chosenRestaurants == null || predictions == null) {
            return;
        }

        List<RestaurantMarker> restaurantsMarkers = new ArrayList<>();
        boolean isProgressBarVisible = restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.LOADING;
        isRetryBarVisibleSingleLiveEvent.setValue(false);
        List<PredictionItemViewState> predictionItemViewStates = mapPredictions(predictions);

        if (restaurantResponseWrapper.getNearbySearchResponse() != null
            && restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.SUCCESS) {

            isProgressBarVisible = false;

            map(restaurantResponseWrapper, restaurantsMarkers, chosenRestaurants);

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
                    predictionItemViewStates,
                    isProgressBarVisible
                )
            );
        }
    }

    @NonNull
    private List<PredictionItemViewState> mapPredictions(@NonNull List<PredictionResponse> predictions) {
        List<PredictionItemViewState> predictionItemViewStateList = new ArrayList<>();
        for (PredictionResponse predictionResponse : predictions) {
            predictionItemViewStateList.add(
                new PredictionItemViewState(
                    predictionResponse.getPlaceId(),
                    predictionResponse.getDescription()
                )
            );
        }
        return predictionItemViewStateList;
    }

    private void map(
        @NonNull RestaurantResponseWrapper restaurantResponseWrapper,
        @NonNull List<RestaurantMarker> restaurantsMarkers,
        @NonNull Collection<String> chosenRestaurants
    ) {
        if (restaurantResponseWrapper.getNearbySearchResponse() != null) {
            for (RestaurantResponse response : restaurantResponseWrapper.getNearbySearchResponse().getResults()) {
                if (response.getBusinessStatus() != null && response.getBusinessStatus().equals("OPERATIONAL")) {

                    int drawableRes = R.drawable.ic_restaurant_red_marker;
                    if (chosenRestaurants.contains(response.getPlaceId())) {
                        drawableRes = R.drawable.ic_restaurant_green_marker;
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
        }
    }

    public SingleLiveEvent<LatLng> getCameraSingleLiveEvent() {
        return cameraSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getIsRetryBarVisibleSingleLiveEvent() {
        return isRetryBarVisibleSingleLiveEvent;
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
        locationModeRepository.setModeUserEnabled(true);
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
        locationModeRepository.setModeUserEnabled(false);
    }
}
