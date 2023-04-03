package com.bakjoul.go4lunch.domain.workmate;

import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;

import java.util.List;

public interface WorkmateRepository {

    LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantsLiveData();

    LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantIdLiveData(String restaurantId);

    LiveData<List<WorkmateEntity>> getAvailableWorkmatesLiveData();
}
