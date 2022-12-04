package com.bakjoul.go4lunch.domain.autocomplete;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.autocomplete.AutocompleteQuery;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;

public interface AutocompleteRepository {

    void setUserQuery(String input);

    void setUserSearchingForWorkmateMode(boolean enabled);

    boolean isUserSearchingForWorkmateMode();

    AutocompleteQuery generateQuery(String userInput, double latitude, double longitude);

    LiveData<String> getUserQuery();

    LiveData<AutocompleteResponse> getAutocomplete(@NonNull String userInput, @NonNull Location location);
}
