package com.bakjoul.go4lunch.domain.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;

import java.util.Collection;

public interface UserRepository {

    void createFirestoreUser(@Nullable LoggedUserEntity currentUser);

    void chooseRestaurant(
        @Nullable LoggedUserEntity currentUser,
        @NonNull String restaurantId,
        @NonNull String restaurantName,
        @NonNull String restaurantAddress
    );

    void unchooseRestaurant(@Nullable LoggedUserEntity currentUser);

    void addRestaurantToFavorites(
        @Nullable LoggedUserEntity currentUser,
        @NonNull String restaurantId,
        @NonNull String restaurantName
    );

    void removeRestaurantFromFavorites(
        @Nullable LoggedUserEntity currentUser,
        @NonNull String restaurantId
    );

    LiveData<UserGoingToRestaurantEntity> getChosenRestaurantLiveData(@Nullable LoggedUserEntity currentUser);

    LiveData<Collection<String>> getFavoritesRestaurantsLiveData(@Nullable LoggedUserEntity currentUser);
}
