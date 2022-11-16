package com.bakjoul.go4lunch.ui.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class DetailsItemViewState {

    @NonNull
    private final String userId;

    @NonNull
    private final String username;

    @Nullable
    private final String userPhotoUrl;

    @NonNull
    private final String message;

    public DetailsItemViewState(@NonNull String userId, @NonNull String username, @Nullable String userPhotoUrl, @NonNull String message) {
        this.userId = userId;
        this.username = username;
        this.userPhotoUrl = userPhotoUrl;
        this.message = message;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailsItemViewState that = (DetailsItemViewState) o;
        return userId.equals(that.userId) && username.equals(that.username) && Objects.equals(userPhotoUrl, that.userPhotoUrl) && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, userPhotoUrl, message);
    }

    @NonNull
    @Override
    public String toString() {
        return "DetailsItemViewState{" +
            "userId='" + userId + '\'' +
            ", username='" + username + '\'' +
            ", userPhotoUrl='" + userPhotoUrl + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}
