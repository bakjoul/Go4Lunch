package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class WorkmateTag {

    @NonNull
    private final String id;

    @Nullable
    private final String chosenRestaurantId;

    @Nullable
    private final String photoUrl;

    @NonNull
    private final String username;


    public WorkmateTag(@NonNull String id, @Nullable String chosenRestaurantId, @Nullable String photoUrl, @NonNull String username) {
        this.id = id;
        this.chosenRestaurantId = chosenRestaurantId;
        this.photoUrl = photoUrl;
        this.username = username;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateTag that = (WorkmateTag) o;
        return id.equals(that.id) && Objects.equals(chosenRestaurantId, that.chosenRestaurantId) && Objects.equals(photoUrl, that.photoUrl) && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chosenRestaurantId, photoUrl, username);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkmateTag{" +
            "id='" + id + '\'' +
            ", chosenRestaurantId='" + chosenRestaurantId + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", username='" + username + '\'' +
            '}';
    }
}
