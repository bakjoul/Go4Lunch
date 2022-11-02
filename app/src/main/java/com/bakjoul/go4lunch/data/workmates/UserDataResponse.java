package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserDataResponse {
    @Nullable
    private final Map<String, Object> chosenRestaurant;

    @Nullable
    private final List<String> favoriteRestaurants;

    public UserDataResponse() {
        this(null, null);
    }

    public UserDataResponse(@Nullable Map<String, Object> chosenRestaurant, @Nullable List<String> favoriteRestaurants) {
        this.chosenRestaurant = chosenRestaurant;
        this.favoriteRestaurants = favoriteRestaurants;
    }

    @Nullable
    public Map<String, Object> getChosenRestaurant() {
        return chosenRestaurant;
    }

    @Nullable
    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDataResponse that = (UserDataResponse) o;
        return Objects.equals(chosenRestaurant, that.chosenRestaurant) && Objects.equals(favoriteRestaurants, that.favoriteRestaurants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chosenRestaurant, favoriteRestaurants);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserDataResponse{" +
            "chosenRestaurant=" + chosenRestaurant +
            ", favoriteRestaurants=" + favoriteRestaurants +
            '}';
    }
}
