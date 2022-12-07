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
    private final List<String> suggestions;

    public MainViewState(@Nullable Uri photoUrl, @NonNull String username, @NonNull String email, @Nullable String chosenRestaurantId, @NonNull List<String> suggestions) {
        this.photoUrl = photoUrl;
        this.username = username;
        this.email = email;
        this.chosenRestaurantId = chosenRestaurantId;
        this.suggestions = suggestions;
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
    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainViewState that = (MainViewState) o;
        return Objects.equals(photoUrl, that.photoUrl) && username.equals(that.username) && email.equals(that.email) && Objects.equals(chosenRestaurantId, that.chosenRestaurantId) && suggestions.equals(that.suggestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, username, email, chosenRestaurantId, suggestions);
    }

    @NonNull
    @Override
    public String toString() {
        return "MainViewState{" +
            "photoUrl=" + photoUrl +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", chosenRestaurantId='" + chosenRestaurantId + '\'' +
            ", suggestions=" + suggestions +
            '}';
    }
}
