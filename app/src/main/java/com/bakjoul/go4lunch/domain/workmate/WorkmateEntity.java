package com.bakjoul.go4lunch.domain.workmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class WorkmateEntity {

    @NonNull
    private final String id;

    @NonNull
    private final String username;

    @NonNull
    private final String email;

    @Nullable
    private final String photoUrl;

    public WorkmateEntity(@NonNull String id, @NonNull String username, @NonNull String email, @Nullable String photoUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    @NonNull
    public String getId() {
        return id;
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
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkmateEntity that = (WorkmateEntity) o;
        return id.equals(that.id) && username.equals(that.username) && email.equals(that.email) && Objects.equals(photoUrl, that.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, photoUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkmateEntity{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
