package com.bakjoul.go4lunch.data.api;

import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;
import com.bakjoul.go4lunch.data.details.model.DetailsResponse;
import com.bakjoul.go4lunch.data.restaurants.model.NearbySearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApis {
    @GET("nearbysearch/json")
    Call<NearbySearchResponse> getNearbyRestaurants(
        @Query("location") String location,
        @Query("rankby") String rankby,
        @Query("type") String type,
        @Query("key") String key
    );

    @GET("details/json")
    Call<DetailsResponse> getRestaurantDetails(
        @Query("place_id") String placeId,
        @Query("key") String key
    );

    @GET("autocomplete/json")
    Call<AutocompleteResponse> getRestaurantAutocomplete(
        @Query("input") String userQuery,
        @Query("location") String location,
        @Query("radius") String radius,
        @Query("type") String type,
        @Query("language") String language,
        @Query("key") String key
    );
}
