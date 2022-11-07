package com.bakjoul.go4lunch.domain.workmate;

import androidx.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

public interface WorkmateRepository {

    LiveData<List<WorkmateEntity>> getAvailableWorkmatesLiveData();

    LiveData<Collection<String>> getChosenRestaurantsLiveData();

    LiveData<List<WorkmateEntity>> getWorkmatesForRestaurantIdLiveData(String restaurantId);

}
