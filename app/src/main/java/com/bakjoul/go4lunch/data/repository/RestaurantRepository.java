package com.bakjoul.go4lunch.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.api.RestaurantSearchService;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RestaurantRepository {

    private static final String TAG = "RestaurantRepository";

    public static final String RANK_BY = "distance";
    public static final String TYPE = "restaurant";

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    @NonNull
    private final RestaurantSearchService restaurantSearchService;

    @Inject
    public RestaurantRepository(@NonNull RestaurantSearchService restaurantSearchService) {
        this.restaurantSearchService = restaurantSearchService;
    }

    public LiveData<NearbySearchResponse> getNearbySearchResponse(
        String location,
        String rankBy,
        String type,
        String key
    ) {
        Call<NearbySearchResponse> call = restaurantSearchService.getRestaurants(location, rankBy, type, key);

        final MutableLiveData<NearbySearchResponse> restaurantsData = new MutableLiveData<>();
        call.enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearchResponse> call, @NonNull Response<NearbySearchResponse> response) {
                restaurantsData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearchResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
                restaurantsData.setValue(null);
            }
        });
        return restaurantsData;
    }
}
