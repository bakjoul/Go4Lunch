package com.bakjoul.go4lunch.ui.details;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.data.repository.RestaurantDetailsRepository;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetailsViewModel extends ViewModel {

    private final LiveData<DetailsViewState> detailsViewState;

    @Inject
    public DetailsViewModel(
        @NonNull RestaurantDetailsRepository restaurantDetailsRepository,
        @NonNull SavedStateHandle savedStateHandle
    ) {
        String restaurantId = savedStateHandle.get("restaurantId");

        if (restaurantId != null) {
            detailsViewState = Transformations.switchMap(
                restaurantDetailsRepository.getDetailsResponse(
                    restaurantId,
                    BuildConfig.MAPS_API_KEY
                ),
                response -> {
                    if (response != null) {
                        return new MutableLiveData<>(
                            map(response)
                        );
                    }
                    return null;
                }
            );
        } else {
            detailsViewState = getErrorDetailsViewState();
        }

    }

    @NonNull
    private DetailsViewState map(@NonNull DetailsResponse response) {
        return new DetailsViewState(
            response.getResult().getPlaceId(),
            null,
            response.getResult().getName(),
            0,
            true,
            response.getResult().getFormattedAddress(),
            "",
            response.getResult().getFormattedPhoneNumber(),
            response.getResult().getWebsite(),
            new ArrayList<>()
        );
    }

    @NonNull
    private MutableLiveData<DetailsViewState> getErrorDetailsViewState() {
        return new MutableLiveData<>(
            new DetailsViewState(
                "",
                null,
                "An error occured",
                0,
                false,
                "",
                "",
                "",
                "",
                null
            )
        );
    }

    public LiveData<DetailsViewState> getDetailsViewState() {
        return detailsViewState;
    }
}
