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
    private final String chosenRestaurant;

    public WorkmateItemViewState(@NonNull String id, @Nullable String photoUrl, @NonNull String name, @Nullable String chosenRestaurant) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.name = name;
        this.chosenRestaurant = chosenRestaurant;
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
    public String getChosenRestaurant() {
        return chosenRestaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateItemViewState that = (WorkmateItemViewState) o;
        return id.equals(that.id) && Objects.equals(photoUrl, that.photoUrl) && name.equals(that.name) && Objects.equals(chosenRestaurant, that.chosenRestaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, photoUrl, name, chosenRestaurant);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkmatesItemViewState{" +
            "id='" + id + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", name='" + name + '\'' +
            ", chosenRestaurant='" + chosenRestaurant + '\'' +
            '}';
    }
}
