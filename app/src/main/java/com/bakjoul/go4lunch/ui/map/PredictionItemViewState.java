package com.bakjoul.go4lunch.ui.map;

import androidx.annotation.NonNull;

import java.util.Objects;

public class PredictionItemViewState {
    @NonNull
    private final String restaurantId;
    @NonNull
    private final String description;

    public PredictionItemViewState(@NonNull String restaurantId, @NonNull String description) {
        this.restaurantId = restaurantId;
        this.description = description;
    }

    @NonNull
    public String getRestaurantId() {
        return restaurantId;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredictionItemViewState that = (PredictionItemViewState) o;
        return restaurantId.equals(that.restaurantId) && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, description);
    }

    @NonNull
    @Override
    public String toString() {
        return "PredictionItemViewState{" +
            "restaurantId='" + restaurantId + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
