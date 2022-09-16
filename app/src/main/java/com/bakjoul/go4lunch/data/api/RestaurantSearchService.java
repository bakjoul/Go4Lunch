package com.bakjoul.go4lunch.data.api;

import com.bakjoul.go4lunch.data.model.NearbySearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantSearchService {
    @GET("nearbysearch/json")
    Call<NearbySearchResponse> getRestaurants(
        @Query("location") String location,
        @Query("radius") String radius,
        @Query("type") String type,
        @Query("key") String key
    );
}
