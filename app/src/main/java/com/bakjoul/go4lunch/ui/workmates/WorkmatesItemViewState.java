package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class WorkmatesItemViewState {

    @NonNull
    private final String id;

    @Nullable
    private final String photoUrl;

    @NonNull
    private final String name;

    public WorkmatesItemViewState(@NonNull String id, @Nullable String photoUrl, @NonNull String name) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmatesItemViewState that = (WorkmatesItemViewState) o;
        return id.equals(that.id) && Objects.equals(photoUrl, that.photoUrl) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, photoUrl, name);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkmatesItemViewState{" +
            "id='" + id + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
