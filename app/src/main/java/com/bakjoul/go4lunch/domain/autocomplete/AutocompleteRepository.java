package com.bakjoul.go4lunch.domain.autocomplete;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.autocomplete.AutocompleteQuery;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;

public interface AutocompleteRepository {

    void setUserQuery(String input);

    void setUserSearchingForWorkmateMode(boolean enabled);

    void setSearchedRestaurant(String restaurantId);

    boolean isUserSearchingForWorkmateMode();

    AutocompleteQuery generateQuery(String userInput, double latitude, double longitude);

    LiveData<String> getUserQueryLiveData();

    LiveData<AutocompleteResponse> getAutocompleteLiveData(@NonNull String userInput, @NonNull Location location);

    LiveData<String> getSearchedRestaurantLiveData();
}
