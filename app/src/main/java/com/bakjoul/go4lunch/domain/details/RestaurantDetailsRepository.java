package com.bakjoul.go4lunch.domain.details;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.details.model.DetailsResponse;

public interface RestaurantDetailsRepository {

    LiveData<DetailsResponse> getDetailsResponse(@NonNull String restaurantId);
}
