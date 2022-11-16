package com.bakjoul.go4lunch.data.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class UserGoingToRestaurantResponse {
    @Nullable
    private final String id;

    @Nullable
    private final String username;

    @Nullable
    private final String email;

    @Nullable
    private final String photoUrl;

    @Nullable
    private final String chosenRestaurantId;

    @Nullable
    private final String chosenRestaurantName;

    public UserGoingToRestaurantResponse() {
        this(null, null, null, null, null, null);
    }

    public UserGoingToRestaurantResponse(@Nullable String id, @Nullable String username, @Nullable String email, @Nullable String photoUrl, @Nullable String chosenRestaurantId, @Nullable String chosenRestaurantName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
        this.chosenRestaurantId = chosenRestaurantId;
        this.chosenRestaurantName = chosenRestaurantName;
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

    @Nullable
    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    @Nullable
    public String getChosenRestaurantName() {
        return chosenRestaurantName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGoingToRestaurantResponse that = (UserGoingToRestaurantResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(chosenRestaurantId, that.chosenRestaurantId) && Objects.equals(chosenRestaurantName, that.chosenRestaurantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, photoUrl, chosenRestaurantId, chosenRestaurantName);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserGoingToRestaurantResponse{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", chosenRestaurantId='" + chosenRestaurantId + '\'' +
            ", chosenRestaurantName='" + chosenRestaurantName + '\'' +
            '}';
    }
}
