package com.bakjoul.go4lunch.domain.workmate;

import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WorkmateRepository {

    LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantsLiveData();

    LiveData<Collection<String>> getWorkmatesChosenRestaurantsLiveData();

    LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantIdLiveData(String restaurantId);

    LiveData<Map<String, Integer>> getRestaurantsAttendance();

    LiveData<List<WorkmateEntity>> getAvailableWorkmatesLiveData();
}
