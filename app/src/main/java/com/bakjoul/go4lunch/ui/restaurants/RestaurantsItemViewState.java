package com.bakjoul.go4lunch.ui.restaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class RestaurantsItemViewState {

    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private final String openingTimes;
    @NonNull
    private final String distance;
    @NonNull
    private final String attendance;

    private final int rating;

    public RestaurantsItemViewState(@NonNull String id,
                                    @NonNull String name,
                                    @NonNull String openingTimes,
                                    @NonNull String distance,
                                    @NonNull String attendance,
                                    int rating) {
        this.id = id;
        this.name = name;
        this.openingTimes = openingTimes;
        this.distance = distance;
        this.attendance = attendance;
        this.rating = rating;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getOpeningTimes() {
        return openingTimes;
    }

    @NonNull
    public String getDistance() {
        return distance;
    }

    @NonNull
    public String getAttendance() {
        return attendance;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsItemViewState that = (RestaurantsItemViewState) o;
        return rating == that.rating && id.equals(that.id) && name.equals(that.name) && openingTimes.equals(that.openingTimes) && distance.equals(that.distance) && attendance.equals(that.attendance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, openingTimes, distance, attendance, rating);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantsItemViewState{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", openingTimes='" + openingTimes + '\'' +
            ", distance='" + distance + '\'' +
            ", attendance='" + attendance + '\'' +
            ", rating=" + rating +
            '}';
    }
}
