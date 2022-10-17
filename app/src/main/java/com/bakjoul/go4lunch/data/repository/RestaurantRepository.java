package com.bakjoul.go4lunch.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.api.RestaurantApi;
import com.bakjoul.go4lunch.data.model.ErrorType;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResult;

import java.net.SocketTimeoutException;
import java.util.Random;

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

   // For testing
   /*Random random = new Random();
   boolean randomBoolean = false;*/

   public LiveData<NearbySearchResult> getNearbySearchResult(
       String location,
       String rankBy,
       String type,
       String key
   ) {
      // For testing
      //randomBoolean = random.nextInt(2) == 0;
      MutableLiveData<NearbySearchResult> restaurantResultMutableLiveData = new MutableLiveData<>();
      // For testing
      /*if (randomBoolean) {
         Log.d("test", "Request done");*/
         restaurantApi.getRestaurants(location, rankBy, type, key).enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearchResponse> call, @NonNull Response<NearbySearchResponse> response) {
               restaurantResultMutableLiveData.setValue(new NearbySearchResult(response.body(), null));
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearchResponse> call, @NonNull Throwable t) {
               Log.d(TAG, "onFailure: ");
               if (t instanceof SocketTimeoutException) {
                  restaurantResultMutableLiveData.setValue(new NearbySearchResult(null, ErrorType.TIMEOUT));
               } else {
                  restaurantResultMutableLiveData.setValue(new NearbySearchResult(null, null));
               }

            }
         });
/*      }
      // For testing
      else {
         Log.d("test", "Request not done, testing timeout");
         restaurantResultMutableLiveData.setValue(new NearbySearchResult(null, ErrorType.TIMEOUT));
      }*/
      return restaurantResultMutableLiveData;
   }
}
