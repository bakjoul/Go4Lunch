package com.bakjoul.go4lunch.ui.restaurants;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class RestaurantsViewState {

    private final List<RestaurantsItemViewState> restaurantsItemViewStates;
    private final boolean isEmptyStateVisible;

    public RestaurantsViewState(List<RestaurantsItemViewState> restaurantsItemViewStates, boolean isEmptyStateVisible) {
        this.restaurantsItemViewStates = restaurantsItemViewStates;
        this.isEmptyStateVisible = isEmptyStateVisible;
    }

    public List<RestaurantsItemViewState> getRestaurantsItemViewStates() {
        return restaurantsItemViewStates;
    }

    public boolean isEmptyStateVisible() {
        return isEmptyStateVisible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsViewState that = (RestaurantsViewState) o;
        return isEmptyStateVisible == that.isEmptyStateVisible && Objects.equals(restaurantsItemViewStates, that.restaurantsItemViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantsItemViewStates, isEmptyStateVisible);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantsViewState{" +
            "restaurantsItemViewStates=" + restaurantsItemViewStates +
            ", isEmptyStateVisible=" + isEmptyStateVisible +
            '}';
    }
}
