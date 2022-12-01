package com.bakjoul.go4lunch.domain.autocomplete;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.autocomplete.AutocompleteQuery;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;

public interface AutocompleteRepository {

    void setUserQuery(String userInput);

    AutocompleteQuery generateQuery(String userInput, double latitude, double longitude);

    LiveData<String> getUserQuery();

    LiveData<AutocompleteResponse> getAutocompleteResponse(
        @NonNull String userInput,
        @NonNull Location location,
        @NonNull String radius,
        @NonNull String type,
        @NonNull String key
    );
}
