package com.bakjoul.go4lunch.domain.autocomplete;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;
import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;

import java.util.List;

public interface AutocompleteRepository {

    void setSearchedRestaurant(String restaurantName);

    LiveData<String> getSearchedRestaurantLiveData();

    LiveData<List<PredictionResponse>> getPredictionsLiveData(@NonNull String userInput, @NonNull Location location);
}
