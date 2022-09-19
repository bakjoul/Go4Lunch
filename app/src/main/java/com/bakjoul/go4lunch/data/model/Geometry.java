package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Geometry {
    @SerializedName("location")
    @Expose
    private final com.bakjoul.go4lunch.data.model.Location location;

    public Geometry(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geometry geometry = (Geometry) o;
        return Objects.equals(location, geometry.location);
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
