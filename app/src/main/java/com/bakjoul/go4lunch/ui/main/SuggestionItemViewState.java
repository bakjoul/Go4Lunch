package com.bakjoul.go4lunch.ui.main;

import androidx.annotation.NonNull;

import java.util.Objects;

public class SuggestionItemViewState {
    @NonNull
    private final String restaurantId;
    @NonNull
    private final String restaurantName;
    @NonNull
    private final String restaurantAddress;

    public SuggestionItemViewState(@NonNull String restaurantId, @NonNull String restaurantName, @NonNull String restaurantAddress) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
    }

    @NonNull
    public String getRestaurantId() {
        return restaurantId;
    }

    @NonNull
    public String getRestaurantName() {
        return restaurantName;
    }

    @NonNull
    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestionItemViewState that = (SuggestionItemViewState) o;
        return restaurantId.equals(that.restaurantId) && restaurantName.equals(that.restaurantName) && restaurantAddress.equals(that.restaurantAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, restaurantName, restaurantAddress);
    }

    @NonNull
    @Override
    public String toString() {
        return "SuggestionItemViewState{" +
            "restaurantId='" + restaurantId + '\'' +
            ", restaurantName='" + restaurantName + '\'' +
            ", restaurantAddress='" + restaurantAddress + '\'' +
            '}';
    }
}
