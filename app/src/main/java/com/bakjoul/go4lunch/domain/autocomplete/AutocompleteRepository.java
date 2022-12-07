package com.bakjoul.go4lunch.domain.autocomplete;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;

import java.util.List;

public interface AutocompleteRepository {

    void setUserSearch(String userSearch);

    LiveData<String> getUserSearchLiveData();

    LiveData<List<PredictionResponse>> getPredictionsLiveData(@NonNull String userSearch, @NonNull Location location);
}
