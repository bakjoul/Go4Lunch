package com.bakjoul.go4lunch.ui.map;

import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.TYPE;

import android.app.Application;
import android.graphics.Bitmap;
import android.location.Location;

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
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.SvgToBitmap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public MapViewModel(
        @NonNull LocationRepository locationRepository,
        @NonNull RestaurantRepository restaurantRepository,
        @NonNull Application application,
        @NonNull SvgToBitmap svgToBitmap) {

        LiveData<Map<MarkerOptions, String>> restaurantsMarkersLiveData = Transformations.switchMap(
            locationRepository.getCurrentLocation(), new Function<Location, LiveData<Map<MarkerOptions, String>>>() {
                LiveData<NearbySearchResponse> nearbySearchResponseLiveData;
                LiveData<Map<MarkerOptions, String>> markersLiveData;

                @Override
                public LiveData<Map<MarkerOptions, String>> apply(Location location) {
                    if (location != null) {
                        locationLiveData.setValue(location);
                        nearbySearchResponseLiveData = restaurantRepository.getNearbySearchResponse(
                            getLocation(location),
                            RANK_BY,
                            TYPE,
                            BuildConfig.MAPS_API_KEY
                        );

                        markersLiveData = Transformations.map(
                            nearbySearchResponseLiveData, response -> {
                                Map<MarkerOptions, String> restaurantsMarkers = new HashMap<>();
                                if (response != null) {
                                    Bitmap greenMarker = svgToBitmap.getBitmapFromVectorDrawable(application.getApplicationContext(), R.drawable.ic_restaurant_green_marker);
                                    Bitmap redMarker = svgToBitmap.getBitmapFromVectorDrawable(application.getApplicationContext(), R.drawable.ic_restaurant_red_marker);
                                    for (RestaurantResponse r : response.getResults()) {
                                        if (r.getBusinessStatus() != null && r.getBusinessStatus().equals("OPERATIONAL")) {
                                            restaurantsMarkers.put(
                                                new MarkerOptions()
                                                    .position(
                                                        new LatLng(
                                                            r.getGeometry().getLocation().getLat(),
                                                            r.getGeometry().getLocation().getLng()
                                                        )
                                                    )
                                                    .title(r.getName())
                                                    .icon(BitmapDescriptorFactory.fromBitmap(greenMarker)),
                                                r.getPlaceId()
                                            );
                                        }
                                    }
                                } else {
                                    return restaurantsMarkers;
                                }
                                return restaurantsMarkers;
                            }
                        );
                    } else {
                        locationLiveData.setValue(null);
                        return null;
                    }
                    return markersLiveData;
                }
            }
        );

        mapViewStateMediatorLiveData.addSource(locationLiveData, location ->
            combine(location, restaurantsMarkersLiveData.getValue()));
        mapViewStateMediatorLiveData.addSource(restaurantsMarkersLiveData, markerOptions ->
            combine(locationLiveData.getValue(), markerOptions));
    }


    private void combine(@Nullable Location location, @Nullable Map<MarkerOptions, String> markerOptions) {
        if (location == null) {
            return;
        }

        if (markerOptions == null) {
            Map<MarkerOptions, String> emptyList = new HashMap<>();
            mapViewStateMediatorLiveData.setValue(
                new MapViewState(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    emptyList
                )
            );
        } else {
            mapViewStateMediatorLiveData.setValue(
                new MapViewState(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    markerOptions
                )
            );
        }
    }

    public MediatorLiveData<MapViewState> getMapViewStateMediatorLiveData() {
        return mapViewStateMediatorLiveData;
    }

    @NonNull
    private String getLocation(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }
}
