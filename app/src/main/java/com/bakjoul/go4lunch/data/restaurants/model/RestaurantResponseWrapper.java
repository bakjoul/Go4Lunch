package com.bakjoul.go4lunch.data.restaurants.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class RestaurantResponseWrapper {

    @Nullable
    private final NearbySearchResponse nearbySearchResponse;

    @NonNull
    private final State state;

    public RestaurantResponseWrapper(@Nullable NearbySearchResponse nearbySearchResponse, @NonNull State state) {
        this.nearbySearchResponse = nearbySearchResponse;
        this.state = state;
    }

    @Nullable
    public NearbySearchResponse getNearbySearchResponse() {
        return nearbySearchResponse;
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
        return Objects.equals(nearbySearchResponse, that.nearbySearchResponse) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nearbySearchResponse, state);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantResponseWrapper{" +
            "nearbySearchResponse=" + nearbySearchResponse +
            ", state=" + state +
            '}';
    }

    public enum State {
        LOADING,
        SUCCESS,
        LOCATION_NULL,
        CRITICAL_ERROR,
        IO_ERROR
    }
}
