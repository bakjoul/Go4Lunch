package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class WorkmateResponse {

    @Nullable
    private final String id;

    @Nullable
    private final String username;

    @Nullable
    private final String email;

    @Nullable
    private final String photoUrl;

    public WorkmateResponse() {
        this(null, null, null, null);
    }

    public WorkmateResponse(@Nullable String id, @Nullable String username, @Nullable String email, @Nullable String photoUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateResponse workmateResponse = (WorkmateResponse) o;
        return Objects.equals(id, workmateResponse.id) && Objects.equals(username, workmateResponse.username) && Objects.equals(email, workmateResponse.email) && Objects.equals(photoUrl, workmateResponse.photoUrl) && Objects.equals(chosenRestaurant, workmateResponse.chosenRestaurant) && Objects.equals(likedRestaurants, workmateResponse.likedRestaurants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "Workmate2{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
