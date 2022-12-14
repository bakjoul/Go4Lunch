package com.bakjoul.go4lunch.domain.restaurants;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.restaurants.NearbySearchQuery;
import com.bakjoul.go4lunch.data.restaurants.RestaurantResponseWrapper;

public interface RestaurantRepository {

    LiveData<RestaurantResponseWrapper> getNearbyRestaurants(Location location, String rankBy, String type, String key);

    NearbySearchQuery generateQuery(double latitude, double longitude);

    String locationToString(@NonNull Location location);
}
