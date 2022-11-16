package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class LocationResponse {
    @SerializedName("lat")
    @Expose
    private final Double lat;

    @SerializedName("lng")
    @Expose
    private final Double lng;

    public LocationResponse(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationResponse location = (LocationResponse) o;
        return Objects.equals(lat, location.lat) && Objects.equals(lng, location.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

    @NonNull
    @Override
    public String toString() {
        return "Location{" +
            "lat=" + lat +
            ", lng=" + lng +
            '}';
    }
}
