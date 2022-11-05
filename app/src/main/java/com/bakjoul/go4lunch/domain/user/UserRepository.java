package com.bakjoul.go4lunch.domain.user;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;

public interface UserRepository {

    void initCurrentUser();

    WorkmateResponse getCurrentUser();

    void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName);

    void unchooseRestaurant(@NonNull String restaurantId);

    void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName);

    void removeRestaurantFromFavorites(@NonNull String restaurantId);
}
