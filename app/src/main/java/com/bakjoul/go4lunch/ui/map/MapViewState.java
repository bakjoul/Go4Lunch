package com.bakjoul.go4lunch.ui.map;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

public class MapViewState {

    private final Location location;
    private final List<MarkerOptions> restaurantsMarkers;

    public MapViewState(Location location, List<MarkerOptions> restaurantsMarkers) {
        this.location = location;
        this.restaurantsMarkers = restaurantsMarkers;
    }

    public Location getLocation() {
        return location;
    }

    public List<MarkerOptions> getRestaurantsMarkers() {
        return restaurantsMarkers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapViewState that = (MapViewState) o;
        return Objects.equals(location, that.location) && Objects.equals(restaurantsMarkers, that.restaurantsMarkers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, restaurantsMarkers);
    }

    @NonNull
    @Override
    public String toString() {
        return "MapViewState{" +
            "location=" + location +
            ", restaurantsMarkers=" + restaurantsMarkers +
            '}';
    }
}
