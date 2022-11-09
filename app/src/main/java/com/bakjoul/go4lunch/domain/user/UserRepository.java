package com.bakjoul.go4lunch.domain.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;

import java.util.Collection;

public interface UserRepository {

    void createFirestoreUser();

    void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName);

    void unchooseRestaurant(@NonNull String restaurantId);

    void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName);

    void removeRestaurantFromFavorites(@NonNull String restaurantId);

    WorkmateResponse getCurrentUser();

    LiveData<String> getChosenRestaurantLiveData();

    LiveData<Collection<String>> getFavoritesRestaurantsLiveData();
}
