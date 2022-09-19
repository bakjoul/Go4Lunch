package com.bakjoul.go4lunch.ui.restaurants;

import androidx.annotation.NonNull;

import java.util.Objects;

public class RestaurantsItemViewState {

    @NonNull
    private final String id;

    @NonNull
    private final String name;

    @NonNull
    private final String location;

    @NonNull
    private final String isOpen;

    @NonNull
    private final String distance;

    @NonNull
    private final String attendance;

    private final float rating;

    private final int ratingBarVisibility;

    @NonNull
    private final String photoUrl;

    public RestaurantsItemViewState(@NonNull String id, @NonNull String name, @NonNull String location, @NonNull String isOpen, @NonNull String distance, @NonNull String attendance, float rating, int ratingBarVisibility, @NonNull String photoUrl) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.isOpen = isOpen;
        this.distance = distance;
        this.attendance = attendance;
        this.rating = rating;
        this.ratingBarVisibility = ratingBarVisibility;
        this.photoUrl = photoUrl;
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
    public String getIsOpen() {
        return isOpen;
    }

    @NonNull
    public String getDistance() {
        return distance;
    }

    @NonNull
    public String getAttendance() {
        return attendance;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingBarVisibility() {
        return ratingBarVisibility;
    }

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsItemViewState that = (RestaurantsItemViewState) o;
        return Float.compare(that.rating, rating) == 0 && ratingBarVisibility == that.ratingBarVisibility && id.equals(that.id) && name.equals(that.name) && location.equals(that.location) && isOpen.equals(that.isOpen) && distance.equals(that.distance) && attendance.equals(that.attendance) && photoUrl.equals(that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, isOpen, distance, attendance, rating, ratingBarVisibility, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantsItemViewState{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", location='" + location + '\'' +
            ", isOpen='" + isOpen + '\'' +
            ", distance='" + distance + '\'' +
            ", attendance='" + attendance + '\'' +
            ", rating=" + rating +
            ", ratingBarVisibility=" + ratingBarVisibility +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
