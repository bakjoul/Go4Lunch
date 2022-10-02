package com.bakjoul.go4lunch.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.api.RestaurantApi;
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

    @NonNull
    private final RestaurantApi restaurantApi;

    @Inject
    public RestaurantRepository(@NonNull RestaurantApi restaurantApi) {
        this.restaurantApi = restaurantApi;
    }

    public LiveData<NearbySearchResponse> getNearbySearchResponse(
        String location,
        String rankBy,
        String type,
        String key
    ) {
        MutableLiveData<NearbySearchResponse> restaurantResponseMutableLiveData = new MutableLiveData<>();
        restaurantApi.getRestaurants(location, rankBy, type, key).enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearchResponse> call, @NonNull Response<NearbySearchResponse> response) {
                restaurantResponseMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearchResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
                restaurantResponseMutableLiveData.setValue(null);
            }
        });
        return restaurantResponseMutableLiveData;
    }
}
