package com.bakjoul.go4lunch.domain.user;

import androidx.annotation.NonNull;

import java.util.Objects;

public class UserGoingToRestaurantEntity {
    @NonNull
    private final String id;

    @NonNull
    private final String username;

    @NonNull
    private final String email;

    @NonNull
    private final String photoUrl;

    @NonNull
    private final String chosenRestaurantId;

    @NonNull
    private final String chosenRestaurantName;

    @NonNull
    private final String chosenRestaurantAddress;

    public UserGoingToRestaurantEntity(@NonNull String id, @NonNull String username, @NonNull String email, @NonNull String photoUrl, @NonNull String chosenRestaurantId, @NonNull String chosenRestaurantName, @NonNull String chosenRestaurantAddress) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
        this.chosenRestaurantId = chosenRestaurantId;
        this.chosenRestaurantName = chosenRestaurantName;
        this.chosenRestaurantAddress = chosenRestaurantAddress;
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

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
    }

    @NonNull
    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    @NonNull
    public String getChosenRestaurantName() {
        return chosenRestaurantName;
    }

    @NonNull
    public String getChosenRestaurantAddress() {
        return chosenRestaurantAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGoingToRestaurantEntity entity = (UserGoingToRestaurantEntity) o;
        return id.equals(entity.id) && username.equals(entity.username) && email.equals(entity.email) && photoUrl.equals(entity.photoUrl) && chosenRestaurantId.equals(entity.chosenRestaurantId) && chosenRestaurantName.equals(entity.chosenRestaurantName) && chosenRestaurantAddress.equals(entity.chosenRestaurantAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, photoUrl, chosenRestaurantId, chosenRestaurantName, chosenRestaurantAddress);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserGoingToRestaurantEntity{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", photoUrl='" + photoUrl + '\'' +
            ", chosenRestaurantId='" + chosenRestaurantId + '\'' +
            ", chosenRestaurantName='" + chosenRestaurantName + '\'' +
            ", chosenRestaurantAddress='" + chosenRestaurantAddress + '\'' +
            '}';
    }
}
