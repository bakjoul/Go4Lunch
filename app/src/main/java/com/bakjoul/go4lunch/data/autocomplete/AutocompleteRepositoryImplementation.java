package com.bakjoul.go4lunch.data.autocomplete;

import android.location.Location;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;
import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;
import com.bakjoul.go4lunch.data.utils.LocationUtils;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AutocompleteRepositoryImplementation implements AutocompleteRepository {

    private static final String TAG = "AutocompleteRepoImpleme";

    private static final int GPS_SCALE = 2;
    private static final String RADIUS = "2000";
    private static final String TYPE = "restaurant";
    private static final String LANGUAGE = "fr";

    @NonNull
    private final GoogleApis googleApis;

    private final MutableLiveData<String> userQueryLiveData = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> isUserSearchingForWorkmateLiveData = new MutableLiveData<>(false);

    private final MutableLiveData<String> searchedRestaurantLiveData = new MutableLiveData<>("");

    private final LruCache<AutocompleteQuery, AutocompleteResponse> lruCache = new LruCache<>(500);

    @Inject
    public AutocompleteRepositoryImplementation(@NonNull GoogleApis googleApis) {
        this.googleApis = googleApis;
    }

    @Override
    public void setUserQuery(String input) {
        userQueryLiveData.setValue(input);
    }

    @Override
    public LiveData<List<PredictionResponse>> getPredictionsLiveData(
        @NonNull String userInput,
        @NonNull Location location
    ) {
        if (isUserSearchingForWorkmateMode()) {
            // TODO
            return new MutableLiveData<>(null);
        }
        MutableLiveData<List<PredictionResponse>> responseMutableLiveData = new MutableLiveData<>();

        AutocompleteQuery query = generateQuery(userInput, location.getLatitude(), location.getLongitude());
        AutocompleteResponse existingResponse = lruCache.get(query);

        if (existingResponse != null) {
            responseMutableLiveData.setValue(existingResponse.getPredictions());
        } else {
            googleApis.getRestaurantAutocomplete(userInput, LocationUtils.locationToString(location), RADIUS, TYPE, LANGUAGE, BuildConfig.MAPS_API_KEY).enqueue(new Callback<AutocompleteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AutocompleteResponse> call, @NonNull Response<AutocompleteResponse> response) {
                    AutocompleteResponse body = response.body();
                    if (body != null) {
                        if (response.isSuccessful() && body.getStatus().equals("OK")) {
                            lruCache.put(query, body);
                        }
                        responseMutableLiveData.setValue(body.getPredictions());
                    } else {
                        responseMutableLiveData.setValue(new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AutocompleteResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    responseMutableLiveData.setValue(new ArrayList<>());
                }
            });
        }

        return responseMutableLiveData;
    }

    @Override
    public LiveData<String> getSearchedRestaurantLiveData() {
        return searchedRestaurantLiveData;
    }

    @Override
    public void setUserSearchingForWorkmateMode(boolean userSearchingForWorkmate) {
        isUserSearchingForWorkmateLiveData.setValue(userSearchingForWorkmate);
    }

    @Override
    public void setSearchedRestaurant(String restaurantId) {
        searchedRestaurantLiveData.setValue(restaurantId);
    }

    @Override
    public boolean isUserSearchingForWorkmateMode() {
        //noinspection ConstantConditions This MutableLiveData always has a value
        return isUserSearchingForWorkmateLiveData.getValue();
    }

    private AutocompleteQuery generateQuery(String userInput, double latitude, double longitude) {
        return new AutocompleteQuery(
            userInput,
            BigDecimal.valueOf(latitude).setScale(GPS_SCALE, RoundingMode.HALF_UP),
            BigDecimal.valueOf(longitude).setScale(GPS_SCALE, RoundingMode.HALF_UP)
        );
    }

    @Override
    public LiveData<String> getUserQueryLiveData() {
        return userQueryLiveData;
    }
}
