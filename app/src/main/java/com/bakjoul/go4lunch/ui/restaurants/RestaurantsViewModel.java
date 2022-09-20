package com.bakjoul.go4lunch.ui.restaurants;

import android.location.Location;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantsViewModel extends ViewModel {

    private static final String RANKBY = "distance";
    private static final String TYPE = "restaurant";
    private static final String BUSINESS_STATUS_OPERATIONAL = "OPERATIONAL";

    @NonNull
    private final RestaurantImageMapper restaurantImageMapper;

    private final LiveData<RestaurantsViewState> restaurantsViewState;

    @Inject
    public RestaurantsViewModel(
        @NonNull RestaurantRepository restaurantRepository,
        @NonNull LocationRepository locationRepository,
        @NonNull RestaurantImageMapper restaurantImageMapper
    ) {
        this.restaurantImageMapper = restaurantImageMapper;

        restaurantsViewState = Transformations.switchMap(
            locationRepository.getCurrentLocation(), location -> {
                if (location != null) {
                    LiveData<NearbySearchResponse> nearbySearchResponseLiveData = restaurantRepository.getNearbySearchResponse(
                        getLocation(location),
                        RANKBY,
                        TYPE,
                        BuildConfig.MAPS_API_KEY
                    );
                    return Transformations.map(
                        nearbySearchResponseLiveData,
                        response -> {
                            List<RestaurantsItemViewState> restaurantsItemViewStateList;
                            if (response != null) {
                                restaurantsItemViewStateList = mapData(response, location);
                            } else {
                                restaurantsItemViewStateList = new ArrayList<>();
                            }
                            return new RestaurantsViewState(restaurantsItemViewStateList, restaurantsItemViewStateList.isEmpty());
                        }
                    );
                } else {
                    return new MutableLiveData<>(new RestaurantsViewState(new ArrayList<>(), true));
                }
            }
        );
    }

    private List<RestaurantsItemViewState> mapData(
        @NonNull NearbySearchResponse response,
        Location location
    ) {
        List<RestaurantsItemViewState> restaurantsItemViewStateList = new ArrayList<>();

        for (RestaurantResponse r : response.getResults()) {
            if (BUSINESS_STATUS_OPERATIONAL.equals(r.getBusinessStatus())) {
                restaurantsItemViewStateList.add(
                    new RestaurantsItemViewState(
                        r.getPlaceId(),
                        r.getName(),
                        r.getVicinity(),
                        checkIfOpen(r.getOpeningHours()),
                        getDistance(location, r.getGeometry().getLocation()),
                        "",
                        getRating(r.getRating()),
                        isRatingBarVisible(r.getUserRatingsTotal()),
                        getPhotoUrl(r.getPhotos())
                    )
                );
            }
        }

        return restaurantsItemViewStateList;
    }

    @NonNull
    private String getLocation(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    @NonNull
    private String checkIfOpen(OpeningHoursResponse openingHoursResponse) {
        String isOpen;
        if (openingHoursResponse != null) {
            if (openingHoursResponse.getOpenNow()) {
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
    private String getDistance(@NonNull Location currentLocation, @NonNull LocationResponse restaurantLocationResponse) {
        double distance = SphericalUtil.computeDistanceBetween(
            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
            new LatLng(restaurantLocationResponse.getLat(), restaurantLocationResponse.getLng())
        );
        return String.format(Locale.getDefault(), "%.0fm", distance);
    }

    private float getRating(double restaurantRating) {
        return (float) Math.round(((restaurantRating * 3 / 5) / 0.5) * 0.5);
    }

    private boolean isRatingBarVisible(int userRatingsTotal) {
        return userRatingsTotal > 0;
    }

    @Nullable
    private String getPhotoUrl(List<PhotoResponse> photoResponses) {
        if (photoResponses != null) {
            String photoRef = photoResponses.get(0).getPhotoReference();
            if (photoRef != null) {
                return restaurantImageMapper.getImageUrl(photoRef);
            }
        }

        return null;
    }

    public LiveData<RestaurantsViewState> getRestaurantsViewState() {
        return restaurantsViewState;
    }
}
