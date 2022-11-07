package com.bakjoul.go4lunch.domain.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;

public interface UserRepository {

    public void createFirestoreUser();

    public LiveData<WorkmateResponse> getCurrentUser();

    public void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName);

    public void unchooseRestaurant(@NonNull String restaurantId);

    public void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName);

    public void removeRestaurantFromFavorites(@NonNull String restaurantId);
}
