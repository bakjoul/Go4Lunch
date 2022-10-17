package com.bakjoul.go4lunch.data.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bakjoul.go4lunch.data.model.NearbySearchResult;

import java.util.Objects;

public class RestaurantResponseWrapper {

    @Nullable
    private final NearbySearchResult nearbySearchResult;

    @NonNull
    private final State state;

    public RestaurantResponseWrapper(@Nullable NearbySearchResult nearbySearchResult, @NonNull State state) {
        this.nearbySearchResult = nearbySearchResult;
        this.state = state;
    }

    @Nullable
    public NearbySearchResult getNearbySearchResult() {
        return nearbySearchResult;
    }

    @NonNull
    public State getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantResponseWrapper that = (RestaurantResponseWrapper) o;
        return Objects.equals(nearbySearchResult, that.nearbySearchResult) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nearbySearchResult, state);
    }

    @Override
    public String toString() {
        return "RestaurantResponseWrapper{" +
            "nearbySearchResult=" + nearbySearchResult +
            ", state=" + state +
            '}';
    }

    public enum State {
        LOADING,
        SUCCESS,
        CRITICAL_ERROR,
        IO_ERROR
    }
}
