package com.bakjoul.go4lunch.data.details;

import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.api.RestaurantApi;
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.domain.details.RestaurantDetailsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RestaurantDetailsRepositoryImplementation implements RestaurantDetailsRepository {

    private static final String TAG = "RestaurantDetailsReposi";

    @NonNull
    private final RestaurantApi restaurantApi;

    private final LruCache<String, DetailsResponse> lruCache = new LruCache<>(500);

    @Inject
    public RestaurantDetailsRepositoryImplementation(@NonNull RestaurantApi restaurantApi) {
        this.restaurantApi = restaurantApi;
    }

    @Override
    public LiveData<DetailsResponse> getDetailsResponse(@NonNull String restaurantId, @NonNull String key) {
        MutableLiveData<DetailsResponse> detailsResponseMutableLiveData = new MutableLiveData<>();

        DetailsResponse existingResponse = lruCache.get(restaurantId);
        if (existingResponse != null) {
            detailsResponseMutableLiveData.setValue(existingResponse);
        } else {
            restaurantApi.getRestaurantDetails(restaurantId, key).enqueue(new Callback<DetailsResponse>() {
                @Override
                public void onResponse(@NonNull Call<DetailsResponse> call, @NonNull Response<DetailsResponse> response) {
                    DetailsResponse body = response.body();
                    // Saves response in cache
                    if (response.isSuccessful() && body != null && body.getStatus().equals("OK")) {
                        lruCache.put(restaurantId, body);
                    }
                    detailsResponseMutableLiveData.setValue(body);
                }

                @Override
                public void onFailure(@NonNull Call<DetailsResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: ");
                    detailsResponseMutableLiveData.setValue(null);
                }
            });
        }
        return detailsResponseMutableLiveData;
    }
}
