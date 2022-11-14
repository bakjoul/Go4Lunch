package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class WorkmateItemViewState {

    @NonNull
    private final String id;

    @Nullable
    private final String photoUrl;

    @NonNull
    private final String name;

    @Nullable
    private final String chosenRestaurantId;

    @Nullable
    private final String chosenRestaurantName;

    public WorkmateItemViewState(@NonNull String id, @Nullable String photoUrl, @NonNull String name, @Nullable String chosenRestaurantId, @Nullable String chosenRestaurantName) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.name = name;
        this.chosenRestaurantId = chosenRestaurantId;
        this.chosenRestaurantName = chosenRestaurantName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    @NonNull
    public String getName() {
        return name;
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
        WorkmateItemViewState that = (WorkmateItemViewState) o;
        return id.equals(that.id) && Objects.equals(photoUrl, that.photoUrl) && name.equals(that.name) && Objects.equals(chosenRestaurantId, that.chosenRestaurantId) && Objects.equals(chosenRestaurantName, that.chosenRestaurantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, photoUrl, name, chosenRestaurantId, chosenRestaurantName);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkmateItemViewState{" +
            "id='" + id + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", name='" + name + '\'' +
            ", chosenRestaurantId='" + chosenRestaurantId + '\'' +
            ", chosenRestaurantName='" + chosenRestaurantName + '\'' +
            '}';
    }
}
