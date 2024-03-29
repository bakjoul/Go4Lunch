package com.bakjoul.go4lunch.data.autocomplete;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteQuery;
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

import dagger.hilt.android.qualifiers.ApplicationContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AutocompleteRepositoryImplementation implements AutocompleteRepository {

    private static final String TAG = "AutocompleteRepoImpleme";

    private static final int GPS_SCALE = 2;
    private static final String RADIUS = "2000";
    private static final String TYPE = "restaurant";

    @NonNull
    private final Context context;

    @NonNull
    private final GoogleApis googleApis;

    private final MutableLiveData<String> userSearchLiveData = new MutableLiveData<>(null);

    private final LruCache<AutocompleteQuery, AutocompleteResponse> lruCache = new LruCache<>(500);

    @Inject
    public AutocompleteRepositoryImplementation(
        @NonNull @ApplicationContext Context context,
        @NonNull GoogleApis googleApis
    ) {
        this.context = context;
        this.googleApis = googleApis;
    }

    @Override
    public LiveData<List<PredictionResponse>> getPredictionsLiveData(
        @NonNull String userInput,
        @NonNull Location location
    ) {
        MutableLiveData<List<PredictionResponse>> responseMutableLiveData = new MutableLiveData<>();

        AutocompleteQuery query = generateQuery(userInput, location.getLatitude(), location.getLongitude());
        AutocompleteResponse existingResponse = lruCache.get(query);

        if (existingResponse != null) {
            responseMutableLiveData.setValue(existingResponse.getPredictions());
        } else {
            googleApis.getRestaurantAutocomplete(
                userInput,
                LocationUtils.locationToString(location),
                RADIUS,
                TYPE,
                context.getString(R.string.language),
                BuildConfig.MAPS_API_KEY
            ).enqueue(new Callback<AutocompleteResponse>() {
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
                    Log.d(TAG, "onFailure: ", t);
                    responseMutableLiveData.setValue(new ArrayList<>());
                }
            });
        }

        return responseMutableLiveData;
    }

    @Override
    public void setUserSearch(String userSearch) {
        userSearchLiveData.setValue(userSearch);
    }

    @Override
    public LiveData<String> getUserSearchLiveData() {
        return userSearchLiveData;
    }

    @NonNull
    private AutocompleteQuery generateQuery(String userInput, double latitude, double longitude) {
        return new AutocompleteQuery(
            userInput,
            BigDecimal.valueOf(latitude).setScale(GPS_SCALE, RoundingMode.HALF_UP),
            BigDecimal.valueOf(longitude).setScale(GPS_SCALE, RoundingMode.HALF_UP)
        );
    }
}
