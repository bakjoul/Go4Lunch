package com.bakjoul.go4lunch.data.common_model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class PhotoResponse {
    @SerializedName("photo_reference")
    @Expose
    private final String photoReference;

    public PhotoResponse(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoResponse photoResponse = (PhotoResponse) o;
        return Objects.equals(photoReference, photoResponse.photoReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoReference);
    }

    @NonNull
    @Override
    public String toString() {
        return "Photo{" +
            "photoReference='" + photoReference + '\'' +
            '}';
    }
}
