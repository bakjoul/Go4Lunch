package com.bakjoul.go4lunch.ui.restaurants;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class RestaurantsItemViewState {

    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private final String location;
    @NonNull
    private final String openingTimes;
    @NonNull
    private final String distance;
    @NonNull
    private final String attendance;

    private final int rating;

    @Nullable
    private final Uri photo;

    public RestaurantsItemViewState(@NonNull String id,
                                    @NonNull String name,
                                    @NonNull String location,
                                    @NonNull String openingTimes,
                                    @NonNull String distance,
                                    @NonNull String attendance,
                                    int rating,
                                    @Nullable Uri photo) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.openingTimes = openingTimes;
        this.distance = distance;
        this.attendance = attendance;
        this.rating = rating;
        this.photo = photo;
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
    public String getLocation() {
        return location;
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

    @Nullable
    public Uri getPhoto() {
        return photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsItemViewState that = (RestaurantsItemViewState) o;
        return rating == that.rating && id.equals(that.id) && name.equals(that.name) && location.equals(that.location) && openingTimes.equals(that.openingTimes) && distance.equals(that.distance) && attendance.equals(that.attendance) && Objects.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, openingTimes, distance, attendance, rating, photo);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantsItemViewState{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", location='" + location + '\'' +
            ", openingTimes='" + openingTimes + '\'' +
            ", distance='" + distance + '\'' +
            ", attendance='" + attendance + '\'' +
            ", rating=" + rating +
            ", photo=" + photo +
            '}';
    }
}
