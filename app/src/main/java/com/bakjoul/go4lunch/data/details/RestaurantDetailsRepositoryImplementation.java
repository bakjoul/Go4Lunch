package com.bakjoul.go4lunch.data.details;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.details.model.DetailsResponse;
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
    private final GoogleApis googleApis;

    private final LruCache<String, DetailsResponse> lruCache = new LruCache<>(500);

    @Inject
    public RestaurantDetailsRepositoryImplementation(@NonNull GoogleApis googleApis) {
        this.googleApis = googleApis;
    }

    @Override
    public LiveData<DetailsResponse> getDetailsResponse(@NonNull String restaurantId) {
        MutableLiveData<DetailsResponse> detailsResponseMutableLiveData = new MutableLiveData<>();

        DetailsResponse existingResponse = lruCache.get(restaurantId);
        if (existingResponse != null) {
            detailsResponseMutableLiveData.setValue(existingResponse);
        } else {
            googleApis.getRestaurantDetails(restaurantId, BuildConfig.MAPS_API_KEY).enqueue(new Callback<DetailsResponse>() {
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
                    Log.d(TAG, "onFailure: ", t);
                    detailsResponseMutableLiveData.setValue(null);
                }
            });
        }
        return detailsResponseMutableLiveData;
    }
}
