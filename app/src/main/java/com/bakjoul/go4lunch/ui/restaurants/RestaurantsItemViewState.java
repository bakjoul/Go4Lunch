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
    private final String address;

    @NonNull
    private final String isOpen;

    @NonNull
    private final String distance;

    @NonNull
    private final String attendance;

    private final float rating;

    private final boolean isRatingBarVisible;

    @Nullable
    private final String photoUrl;

    public RestaurantsItemViewState(@NonNull String id, @NonNull String name, @NonNull String address, @NonNull String isOpen, @NonNull String distance, @NonNull String attendance, float rating, boolean isRatingBarVisible, @Nullable String photoUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.isOpen = isOpen;
        this.distance = distance;
        this.attendance = attendance;
        this.rating = rating;
        this.isRatingBarVisible = isRatingBarVisible;
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
    public String getAddress() {
        return address;
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

    public boolean isRatingBarVisible() {
        return isRatingBarVisible;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantsItemViewState that = (RestaurantsItemViewState) o;
        return Float.compare(that.rating, rating) == 0 && isRatingBarVisible == that.isRatingBarVisible && id.equals(that.id) && name.equals(that.name) && address.equals(that.address) && isOpen.equals(that.isOpen) && distance.equals(that.distance) && attendance.equals(that.attendance) && Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, isOpen, distance, attendance, rating, isRatingBarVisible, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantsItemViewState{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", isOpen='" + isOpen + '\'' +
            ", distance='" + distance + '\'' +
            ", attendance='" + attendance + '\'' +
            ", rating=" + rating +
            ", isRatingBarVisible=" + isRatingBarVisible +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
