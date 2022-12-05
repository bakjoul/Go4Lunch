package com.bakjoul.go4lunch.data.restaurants;

import android.location.Location;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.restaurants.model.NearbySearchQuery;
import com.bakjoul.go4lunch.data.restaurants.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.domain.restaurants.RestaurantRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RestaurantRepositoryImplementation implements RestaurantRepository {

    private static final String TAG = "RestaurantRepoImplement";

    private static final int GPS_SCALE = 2;

    public static final String RANK_BY = "distance";
    public static final String TYPE = "restaurant";

    @NonNull
    private final GoogleApis googleApis;

    private final LruCache<NearbySearchQuery, NearbySearchResponse> lruCache = new LruCache<>(500);

    @Inject
    public RestaurantRepositoryImplementation(@NonNull GoogleApis googleApis) {
        this.googleApis = googleApis;
    }

    // For testing
    Random random = new Random();
    boolean randomBoolean = false;

    @Override
    public LiveData<RestaurantResponseWrapper> getNearbyRestaurants(@NonNull Location location) {
        // For testing
        randomBoolean = random.nextInt(1) == 0;
        //

        MutableLiveData<RestaurantResponseWrapper> wrapperMutableLiveData = new MutableLiveData<>();
        wrapperMutableLiveData.setValue(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.LOADING));

        // For testing
        if (randomBoolean) {
            Log.d("test", "Request initiated");
            //

            NearbySearchQuery query = generateQuery(location.getLatitude(), location.getLongitude());
            NearbySearchResponse existingResponse = lruCache.get(query);

            if (existingResponse != null) {
                wrapperMutableLiveData.setValue(
                    new RestaurantResponseWrapper(
                        existingResponse,
                        RestaurantResponseWrapper.State.SUCCESS
                    )
                );
            } else {
                googleApis.getNearbyRestaurants(locationToString(location), RANK_BY, TYPE, BuildConfig.MAPS_API_KEY).enqueue(new Callback<NearbySearchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NearbySearchResponse> call, @NonNull Response<NearbySearchResponse> response) {
                        NearbySearchResponse body = response.body();

                        if (response.isSuccessful() && body != null && body.getStatus().equals("OK")) {
                            lruCache.put(query, body);
                        }

                        wrapperMutableLiveData.setValue(
                            new RestaurantResponseWrapper(
                                body,
                                RestaurantResponseWrapper.State.SUCCESS
                            )
                        );
                    }

                    @Override
                    public void onFailure(@NonNull Call<NearbySearchResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        if (t instanceof IOException) {
                            wrapperMutableLiveData.setValue(
                                new RestaurantResponseWrapper(
                                    null,
                                    RestaurantResponseWrapper.State.IO_ERROR
                                )
                            );
                        } else {
                            wrapperMutableLiveData.setValue(
                                new RestaurantResponseWrapper(
                                    null,
                                    RestaurantResponseWrapper.State.CRITICAL_ERROR
                                )
                            );
                        }
                    }
                });
            }
        }
        // For testing
        else {
            Log.d("test", "Request failed");
            wrapperMutableLiveData.setValue(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.IO_ERROR));
        }
        //
        return wrapperMutableLiveData;
    }

    @Override
    public NearbySearchQuery generateQuery(double latitude, double longitude) {
        return new NearbySearchQuery(
            BigDecimal.valueOf(latitude).setScale(GPS_SCALE, RoundingMode.HALF_UP),
            BigDecimal.valueOf(longitude).setScale(GPS_SCALE, RoundingMode.HALF_UP)
        );
    }

    @Override
    public String locationToString(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }
}
