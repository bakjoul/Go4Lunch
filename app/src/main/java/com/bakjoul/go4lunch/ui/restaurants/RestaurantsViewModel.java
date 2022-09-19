package com.bakjoul.go4lunch.ui.restaurants;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.OpeningHours;
import com.bakjoul.go4lunch.data.model.Restaurant;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantsViewModel extends ViewModel {

    private static final String RADIUS = "2000";
    private static final String TYPE = "restaurant";

    private final LiveData<RestaurantsViewState> restaurantsViewState;

    @Inject
    public RestaurantsViewModel(@NonNull RestaurantRepository restaurantRepository,
                                @NonNull LocationRepository locationRepository) {
        restaurantsViewState = Transformations.switchMap(
            locationRepository.getCurrentLocation(), location -> {
                LiveData<NearbySearchResponse> nearbySearchResponseLiveData;
                LiveData<RestaurantsViewState> restaurantsViewStateLiveData;
                if (location != null) {
                    nearbySearchResponseLiveData = restaurantRepository.getNearbySearchResponse(
                        getLocation(location),
                        RADIUS,
                        TYPE,
                        BuildConfig.MAPS_API_KEY
                    );
                    restaurantsViewStateLiveData = Transformations.switchMap(
                        nearbySearchResponseLiveData, new Function<NearbySearchResponse, LiveData<RestaurantsViewState>>() {
                            final MutableLiveData<RestaurantsViewState> viewState = new MutableLiveData<>();

                            @Override
                            public LiveData<RestaurantsViewState> apply(NearbySearchResponse response) {
                                List<RestaurantsItemViewState> restaurantsItemViewStateList = new ArrayList<>();
                                if (response != null) {
                                    mapData(response, restaurantsItemViewStateList);
                                    viewState.setValue(new RestaurantsViewState(restaurantsItemViewStateList, restaurantsItemViewStateList.isEmpty()));
                                }
                                return viewState;
                            }
                        }
                    );
                    return restaurantsViewStateLiveData;
                } else {
                    return null;
                }
            }
        );
    }

    private void mapData(@NonNull NearbySearchResponse response, List<RestaurantsItemViewState> restaurantsItemViewStateList) {
        for (Restaurant r : response.getResults()) {
            if (r.getBusiness_status().equals("OPERATIONAL")) {
                restaurantsItemViewStateList.add(
                    new RestaurantsItemViewState(
                        r.getPlaceId(),
                        r.getName(),
                        r.getVicinity(),
                        getIsOpen(r.getOpeningHours()),
                        "",
                        "",
                        2,
                        null
                    )
                );
            }
        }
    }

    @NonNull
    private String getLocation(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    @NonNull
    private String getIsOpen(OpeningHours openingHours) {
        String isOpen;
        if (openingHours != null) {
            if (openingHours.getOpenNow()) {
                isOpen = "Ouvert";
            } else {
                isOpen = "Fermé";
            }
        } else {
            isOpen = "Information indisponible";
        }
        return isOpen;
    }

    public LiveData<RestaurantsViewState> getRestaurantsViewState() {
        return restaurantsViewState;
    }
}
