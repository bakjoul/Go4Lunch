package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.Nullable;

public class WorkmateTag {

    @Nullable
    private final String chosenRestaurantId;

    @Nullable
    private final String workmateId;

    public WorkmateTag(@Nullable String chosenRestaurantId, @Nullable String workmateId) {
        this.chosenRestaurantId = chosenRestaurantId;
        this.workmateId = workmateId;
    }

    @Nullable
    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    @Nullable
    public String getWorkmateId() {
        return workmateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateTag workmateTag = (WorkmateTag) o;
        return Objects.equals(chosenRestaurantId, workmateTag.chosenRestaurantId) && Objects.equals(workmateId, workmateTag.workmateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chosenRestaurantId, workmateId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Tag{" +
            "chosenRestaurantId='" + chosenRestaurantId + '\'' +
            ", workmateId='" + workmateId + '\'' +
            '}';
    }
}
