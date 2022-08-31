package com.bakjoul.go4lunch.ui.main;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MainViewState {

    @NonNull
    private final Uri photoUrl;
    @NonNull
    private final String username;
    @NonNull
    private final String email;

    public MainViewState(@NonNull Uri photoUrl, @NonNull String username, @NonNull String email) {
        this.photoUrl = photoUrl;
        this.username = username;
        this.email = email;
    }

    @NonNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainViewState that = (MainViewState) o;
        return photoUrl.equals(that.photoUrl) && username.equals(that.username) && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, username, email);
    }

    @Override
    public String toString() {
        return "MainActivityViewState{" +
            "photoUrl=" + photoUrl +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
