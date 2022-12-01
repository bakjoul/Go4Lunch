package com.bakjoul.go4lunch.data.autocomplete;

import android.location.Location;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;
import com.bakjoul.go4lunch.data.utils.LocationUtils;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AutocompleteRepositoryImplementation implements AutocompleteRepository {

    private static final String TAG = "AutocompleteRepoImpleme";

    private static final int GPS_SCALE = 2;

    @NonNull
    private final GoogleApis googleApis;

    private final MutableLiveData<String> userQueryLiveData = new MutableLiveData<>();

    private final LruCache<AutocompleteQuery, AutocompleteResponse> lruCache = new LruCache<>(500);

    @Inject
    public AutocompleteRepositoryImplementation(@NonNull GoogleApis googleApis) {
        this.googleApis = googleApis;
    }

    @Override
    public void setUserQuery(String queryText) {
        userQueryLiveData.setValue(queryText);
    }

    @Override
    public AutocompleteQuery generateQuery(String userInput, double latitude, double longitude) {
        return new AutocompleteQuery(
            userInput,
            BigDecimal.valueOf(latitude).setScale(GPS_SCALE, RoundingMode.HALF_UP),
            BigDecimal.valueOf(longitude).setScale(GPS_SCALE, RoundingMode.HALF_UP)
        );
    }

    @Override
    public LiveData<String> getUserQuery() {
        return userQueryLiveData;
    }

    @Override
    public LiveData<AutocompleteResponse> getAutocompleteResponse(
        @NonNull String userInput,
        @NonNull Location location,
        @NonNull String radius,
        @NonNull String type,
        @NonNull String key
    ) {
        MutableLiveData<AutocompleteResponse> responseMutableLiveData = new MutableLiveData<>();

        AutocompleteQuery query = generateQuery(userInput, location.getLatitude(), location.getLongitude());
        AutocompleteResponse existingResponse = lruCache.get(query);

        if (existingResponse != null) {
            responseMutableLiveData.setValue(existingResponse);

        } else {
            googleApis.getRestaurantAutocomplete(userInput, LocationUtils.locationToString(location), radius, type, key).enqueue(new Callback<AutocompleteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AutocompleteResponse> call, @NonNull Response<AutocompleteResponse> response) {
                    AutocompleteResponse body = response.body();
                    if (response.isSuccessful() && body != null && body.getStatus().equals("OK")) {
                        lruCache.put(query, body);
                    }
                    responseMutableLiveData.setValue(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<AutocompleteResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    responseMutableLiveData.setValue(null);
                }
            });
        }

        return responseMutableLiveData;
    }
}
