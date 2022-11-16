package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.restaurants.RestaurantResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class NearbySearchResponse {
    @SerializedName("results")
    @Expose
    private final List<RestaurantResponse> results;
    @SerializedName("status")
    @Expose
    private final String status;

    public NearbySearchResponse(List<RestaurantResponse> results, String status) {
        this.results = results;
        this.status = status;
    }

    public List<RestaurantResponse> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbySearchResponse that = (NearbySearchResponse) o;
        return Objects.equals(results, that.results) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results, status);
    }

    @NonNull
    @Override
    public String toString() {
        return "NearbySearchResponse{" +
            "results=" + results +
            ", status='" + status + '\'' +
            '}';
    }
}
