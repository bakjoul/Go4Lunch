package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class WorkmateResponse {

    @Nullable
    private final String id;

    @Nullable
    private final String username;

    @Nullable
    private final String email;

    @Nullable
    private final String photoUrl;

    public WorkmateResponse() {
        this(null, null, null, null);
    }

    public WorkmateResponse(@Nullable String id, @Nullable String username, @Nullable String email, @Nullable String photoUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateResponse that = (WorkmateResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkmateResponse{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
