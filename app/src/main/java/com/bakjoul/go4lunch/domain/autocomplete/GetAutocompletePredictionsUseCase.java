package com.bakjoul.go4lunch.domain.autocomplete;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;
import com.bakjoul.go4lunch.domain.location.GetUserPositionUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GetAutocompletePredictionsUseCase {

    @NonNull
    private final AutocompleteRepository autocompleteRepository;

    @NonNull
    private final GetUserPositionUseCase getUserPositionUseCase;

    @Inject
    public GetAutocompletePredictionsUseCase(
        @NonNull AutocompleteRepository autocompleteRepository,
        @NonNull GetUserPositionUseCase getUserPositionUseCase
    ) {
        this.autocompleteRepository = autocompleteRepository;
        this.getUserPositionUseCase = getUserPositionUseCase;
    }

    public LiveData<List<PredictionResponse>> invoke(@Nullable String userSearch) {
        if (userSearch == null || userSearch.length() < 3) {
            return new MutableLiveData<>(new ArrayList<>());
        } else {
            return Transformations.switchMap(
                getUserPositionUseCase.invoke(),
                location -> {
                    if (location == null) {
                        return new MutableLiveData<>(new ArrayList<>());
                    }
                    return autocompleteRepository.getPredictionsLiveData(userSearch, location);
                }
            );
        }
    }
}
