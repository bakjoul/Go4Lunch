package com.bakjoul.go4lunch.ui.map;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.restaurants.model.RestaurantMarker;

import java.util.List;
import java.util.Objects;

public class MapViewState {

    @NonNull
    private final List<RestaurantMarker> restaurantsMarkers;
    @NonNull
    private final List<PredictionItemViewState> predictionItemViewStateList;
    private final boolean isProgressBarVisible;

    public MapViewState(@NonNull List<RestaurantMarker> restaurantsMarkers, @NonNull List<PredictionItemViewState> predictionItemViewStateList, boolean isProgressBarVisible) {
        this.restaurantsMarkers = restaurantsMarkers;
        this.predictionItemViewStateList = predictionItemViewStateList;
        this.isProgressBarVisible = isProgressBarVisible;
    }

    @NonNull
    public List<RestaurantMarker> getRestaurantsMarkers() {
        return restaurantsMarkers;
    }

    @NonNull
    public List<PredictionItemViewState> getPredictionItemViewStateList() {
        return predictionItemViewStateList;
    }

    public boolean isProgressBarVisible() {
        return isProgressBarVisible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapViewState that = (MapViewState) o;
        return isProgressBarVisible == that.isProgressBarVisible && restaurantsMarkers.equals(that.restaurantsMarkers) && predictionItemViewStateList.equals(that.predictionItemViewStateList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantsMarkers, predictionItemViewStateList, isProgressBarVisible);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapViewState{" +
            "restaurantsMarkers=" + restaurantsMarkers +
            ", predictionItemViewStateList=" + predictionItemViewStateList +
            ", isProgressBarVisible=" + isProgressBarVisible +
            '}';
    }
}
