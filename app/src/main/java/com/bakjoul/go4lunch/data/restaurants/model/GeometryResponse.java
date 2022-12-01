package com.bakjoul.go4lunch.data.restaurants.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class GeometryResponse {
    @SerializedName("location")
    @Expose
    private final LocationResponse location;

    public GeometryResponse(LocationResponse location) {
        this.location = location;
    }

    public LocationResponse getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeometryResponse geometryResponse = (GeometryResponse) o;
        return Objects.equals(location, geometryResponse.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @NonNull
    @Override
    public String toString() {
        return "Geometry{" +
            "location=" + location +
            '}';
    }
}
