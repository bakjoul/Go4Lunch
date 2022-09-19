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
import com.bakjoul.go4lunch.data.model.Photo;
import com.bakjoul.go4lunch.data.model.Restaurant;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantsViewModel extends ViewModel {

    private static final String RADIUS = "2000";
    private static final String RANKBY = "distance";
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
                        RANKBY,
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
                                    mapData(response, restaurantsItemViewStateList, location);
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

    private void mapData(@NonNull NearbySearchResponse response, List<RestaurantsItemViewState> restaurantsItemViewStateList, Location location) {
        for (Restaurant r : response.getResults()) {
            if (r.getBusinessStatus().equals("OPERATIONAL")) {
                restaurantsItemViewStateList.add(
                    new RestaurantsItemViewState(
                        r.getPlaceId(),
                        r.getName(),
                        r.getVicinity(),
                        checkIfOpen(r.getOpeningHours()),
                        getDistance(location, r.getGeometry().getLocation()),
                        "",
                        getRating(r.getRating()),
                        checkIfRated(r.getUserRatingsTotal()),
                        getPhotoUrl(r.getPhotos())
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
    private String checkIfOpen(OpeningHours openingHours) {
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

    @NonNull
    private String getDistance(@NonNull Location currentLocation, @NonNull com.bakjoul.go4lunch.data.model.Location restaurantLocation) {
        double distance = SphericalUtil.computeDistanceBetween(
            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
            new LatLng(restaurantLocation.getLat(), restaurantLocation.getLng())
        );
        return String.format(Locale.getDefault(), "%.0fm", distance);
    }

    private float getRating(double restaurantRating) {
        return (float) Math.round(((restaurantRating * 3 / 5) / 0.5) * 0.5);
    }

    private int checkIfRated(int userRatingsTotal) {
        int visibility;
        if (userRatingsTotal > 0) {
            visibility = 0;
        } else {
            visibility = 4;
        }
        return visibility;
    }

    @NonNull
    private String getPhotoUrl(List<Photo> photos) {
        String url = RestaurantRepository.BASE_URL + "photo?maxwidth=100&photoreference=";
        String photoRef;
        if (photos != null) {
            photoRef = photos.get(0).getPhotoReference();
            url += photoRef + "&key=" + BuildConfig.MAPS_API_KEY;
        } else {
            url = "";
        }
        return url;
    }

    public LiveData<RestaurantsViewState> getRestaurantsViewState() {
        return restaurantsViewState;
    }
}
