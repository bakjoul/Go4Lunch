package com.bakjoul.go4lunch.domain.workmate;

import androidx.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WorkmateRepository {

    LiveData<List<WorkmateEntity>> getAvailableWorkmatesLiveData();

    LiveData<Collection<String>> getWorkmatesChosenRestaurantsLiveData();

    LiveData<List<WorkmateEntity>> getWorkmatesForRestaurantIdLiveData(String restaurantId);

    LiveData<Map<String, Integer>> getRestaurantsAttendance();

    LiveData<Map<String, String>> getWorkmatesWithChoiceLiveData();
}
