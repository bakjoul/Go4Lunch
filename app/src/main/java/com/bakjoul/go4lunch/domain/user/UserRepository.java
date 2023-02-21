package com.bakjoul.go4lunch.domain.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.Collection;

public interface UserRepository {

    void createFirestoreUser();

    void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName, @NonNull String restaurantAddress);

    void unchooseRestaurant();

    void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName);

    void removeRestaurantFromFavorites(@NonNull String restaurantId);

    LiveData<UserGoingToRestaurantEntity> getChosenRestaurantLiveData();

    LiveData<Collection<String>> getFavoritesRestaurantsLiveData();
}
