package com.bakjoul.go4lunch.ui.main;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class MainViewState {

    @Nullable
    private final Uri photoUrl;
    @NonNull
    private final String username;
    @NonNull
    private final String email;
    @Nullable
    private final String chosenRestaurantId;
    @NonNull
    private final List<SuggestionItemViewState> suggestionItemViewStates;

    public MainViewState(@Nullable Uri photoUrl, @NonNull String username, @NonNull String email, @Nullable String chosenRestaurantId, @NonNull List<SuggestionItemViewState> suggestionItemViewStates) {
        this.photoUrl = photoUrl;
        this.username = username;
        this.email = email;
        this.chosenRestaurantId = chosenRestaurantId;
        this.suggestionItemViewStates = suggestionItemViewStates;
    }

    @Nullable
    public Uri getPhotoUrl() {
        return photoUrl;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    @NonNull
    public List<SuggestionItemViewState> getSuggestionItemViewStates() {
        return suggestionItemViewStates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainViewState that = (MainViewState) o;
        return Objects.equals(photoUrl, that.photoUrl) && username.equals(that.username) && email.equals(that.email) && Objects.equals(chosenRestaurantId, that.chosenRestaurantId) && suggestionItemViewStates.equals(that.suggestionItemViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, username, email, chosenRestaurantId, suggestionItemViewStates);
    }

    @NonNull
    @Override
    public String toString() {
        return "MainViewState{" +
            "photoUrl=" + photoUrl +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", chosenRestaurantId='" + chosenRestaurantId + '\'' +
            ", suggestionItemViewStates=" + suggestionItemViewStates +
            '}';
    }
}
