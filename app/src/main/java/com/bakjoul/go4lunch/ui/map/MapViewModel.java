package com.bakjoul.go4lunch.ui.map;

import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.TYPE;

import android.app.Application;
import android.graphics.Bitmap;
import android.location.Location;
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
import com.bakjoul.go4lunch.data.model.ErrorType;
import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.SvgToBitmap;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    private static final String TAG = "MapViewModel";

    private final MutableLiveData<Boolean> isMapReadyMutableLiveData = new MutableLiveData<>(false);

    private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

    private final MutableLiveData<Boolean> isProgressBarVisibleLiveData = new MutableLiveData<>(true);

    private final SingleLiveEvent<ErrorType> errorTypeSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public MapViewModel(
        @NonNull LocationRepository locationRepository,
        @NonNull RestaurantRepository restaurantRepository,
        @NonNull Application application,
        @NonNull SvgToBitmap svgToBitmap) {

        LiveData<Location> locationLiveData = locationRepository.getCurrentLocation();

        LiveData<List<RestaurantMarker>> restaurantsMarkersLiveData = Transformations.switchMap(
            locationLiveData,
            location -> {
                if (location == null) {
                    return new MutableLiveData<>(null);
                }
                return Transformations.switchMap(
                    nearbySearchRequestPingMutableLiveData,
                    aVoid -> Transformations.map(
                        restaurantRepository.getNearbySearchResult(getLocation(location), RANK_BY, TYPE, BuildConfig.MAPS_API_KEY),
                        result -> {
                            List<RestaurantMarker> restaurantsMarkers = new ArrayList<>();
                            if (result.getResponse() != null) {
                                isProgressBarVisibleLiveData.setValue(false);
                                Bitmap greenMarker = svgToBitmap.getBitmapFromVectorDrawable(application.getApplicationContext(), R.drawable.ic_restaurant_green_marker);
                                Bitmap redMarker = svgToBitmap.getBitmapFromVectorDrawable(application.getApplicationContext(), R.drawable.ic_restaurant_red_marker);
                                for (RestaurantResponse r : result.getResponse().getResults()) {
                                    if (r.getBusinessStatus() != null && r.getBusinessStatus().equals("OPERATIONAL")) {
                                        restaurantsMarkers.add(
                                            new RestaurantMarker(
                                                r.getPlaceId(),
                                                new LatLng(
                                                    r.getGeometry().getLocation().getLat(),
                                                    r.getGeometry().getLocation().getLng()
                                                ),
                                                r.getName(),
                                                BitmapDescriptorFactory.fromBitmap(greenMarker)
                                            )
                                        );
                                    }
                                }
                            } else if (result.getErrorType() == ErrorType.TIMEOUT) {
                                isProgressBarVisibleLiveData.setValue(false);
                                errorTypeSingleLiveEvent.setValue(ErrorType.TIMEOUT);
                                Log.d(TAG, "Socket time out");
                            } else {
                                isProgressBarVisibleLiveData.setValue(false);
                                return restaurantsMarkers;
                            }
                            return restaurantsMarkers;
                        }
                    )
                );
            }
        );

        mapViewStateMediatorLiveData.addSource(locationLiveData, location ->
            combine(location, restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue(), isMapReadyMutableLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(restaurantsMarkersLiveData, markerOptions ->
            combine(locationLiveData.getValue(), markerOptions, isProgressBarVisibleLiveData.getValue(), isMapReadyMutableLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(isProgressBarVisibleLiveData, isProgressBarVisible ->
            combine(locationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisible, isMapReadyMutableLiveData.getValue())
        );
        mapViewStateMediatorLiveData.addSource(isMapReadyMutableLiveData, isMapReady ->
            combine(locationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue(), isMapReady)
        );
    }


    private void combine(
        @Nullable Location location,
        @Nullable List<RestaurantMarker> restaurantMarkers,
        @Nullable Boolean isProgressBarVisible,
        @Nullable Boolean isMapReady
    ) {
        if (location == null || isProgressBarVisible == null || isMapReady == null) {
            return;
        }

        if (isMapReady) {
            mapViewStateMediatorLiveData.setValue(
                new MapViewState(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    restaurantMarkers == null ? new ArrayList<>() : restaurantMarkers,
                    isProgressBarVisible
                )
            );
        }
    }

    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewStateMediatorLiveData;
    }

    public SingleLiveEvent<ErrorType> getErrorTypeSingleLiveEvent() {
        return errorTypeSingleLiveEvent;
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
}
