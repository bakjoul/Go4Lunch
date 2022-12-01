package com.bakjoul.go4lunch.data.restaurants.model;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Objects;

public class NearbySearchQuery {
    @NonNull
    private final BigDecimal latitude;
    @NonNull
    private final BigDecimal longitude;

    public NearbySearchQuery(@NonNull BigDecimal latitude, @NonNull BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbySearchQuery that = (NearbySearchQuery) o;
        return latitude.equals(that.latitude) && longitude.equals(that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
