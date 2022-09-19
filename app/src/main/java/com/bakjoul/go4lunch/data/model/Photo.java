package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Photo {
    @SerializedName("photo_reference")
    @Expose
    private final String photoReference;

    public Photo(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(photoReference, photo.photoReference);
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
