package com.bakjoul.go4lunch.data.repository;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.api.RestaurantSearchService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RestaurantDetailsRepository {

    @NonNull
    private final RestaurantSearchService restaurantSearchService;

    @Inject
    public RestaurantDetailsRepository(@NonNull RestaurantSearchService restaurantSearchService) {
        this.restaurantSearchService = restaurantSearchService;
    }


}
