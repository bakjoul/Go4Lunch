package com.bakjoul.go4lunch.domain.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;

import java.util.Collection;

public interface UserRepository {

    void createFirestoreUser();

    WorkmateResponse getCurrentUser();

    LiveData<String> getChosenRestaurantLiveData();

    void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName);

    void unchooseRestaurant(@NonNull String restaurantId);

    LiveData<Collection<String>> getFavoritesRestaurantsLiveData();

    void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName);

    void removeRestaurantFromFavorites(@NonNull String restaurantId);
}
