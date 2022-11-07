package com.bakjoul.go4lunch.domain.workmate;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface WorkmateRepository {

    LiveData<List<WorkmateEntity>> getAvailableWorkmatesLiveData();

    LiveData<List<String>> getChosenRestaurantsLiveData();

    LiveData<List<WorkmateEntity>> getWorkmatesForRestaurantIdLiveData(String restaurantId);

}
