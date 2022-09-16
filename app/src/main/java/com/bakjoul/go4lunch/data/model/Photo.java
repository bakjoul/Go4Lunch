package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Photo {
    @SerializedName("height")
    @Expose
    private final Integer height;
    @SerializedName("html_attributions")
    @Expose
    private final List<String> htmlAttributions;
    @SerializedName("photo_reference")
    @Expose
    private final String photoReference;
    @SerializedName("width")
    @Expose
    private final Integer width;

    public Photo(Integer height, List<String> htmlAttributions, String photoReference, Integer width) {
        this.height = height;
        this.htmlAttributions = htmlAttributions;
        this.photoReference = photoReference;
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public Integer getWidth() {
        return width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(height, photo.height) && Objects.equals(htmlAttributions, photo.htmlAttributions) && Objects.equals(photoReference, photo.photoReference) && Objects.equals(width, photo.width);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, htmlAttributions, photoReference, width);
    }

    @NonNull
    @Override
    public String toString() {
        return "Photo{" +
            "height=" + height +
            ", htmlAttributions=" + htmlAttributions +
            ", photoReference='" + photoReference + '\'' +
            ", width=" + width +
            '}';
    }
}
