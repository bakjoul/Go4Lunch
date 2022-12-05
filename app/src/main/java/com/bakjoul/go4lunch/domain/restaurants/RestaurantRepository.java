package com.bakjoul.go4lunch.domain.restaurants;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.restaurants.model.NearbySearchQuery;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantResponseWrapper;

public interface RestaurantRepository {

    LiveData<RestaurantResponseWrapper> getNearbyRestaurants(@NonNull Location location);

    NearbySearchQuery generateQuery(double latitude, double longitude);

    String locationToString(@NonNull Location location);
}
