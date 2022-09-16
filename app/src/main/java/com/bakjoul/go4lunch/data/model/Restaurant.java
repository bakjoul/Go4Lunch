package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Restaurant {
    @SerializedName("geometry")
    @Expose
    private final Geometry geometry;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("opening_hours")
    @Expose
    private final OpeningHours openingHours;

    @SerializedName("photos")
    @Expose
    private final List<Photo> photos;

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("rating")
    @Expose
    private final Double rating;

    @SerializedName("vicinity")
    @Expose
    private final String vicinity;

    public Restaurant(Geometry geometry, String name, OpeningHours openingHours, List<Photo> photos, String placeId, Double rating, String vicinity) {
        this.geometry = geometry;
        this.name = name;
        this.openingHours = openingHours;
        this.photos = photos;
        this.placeId = placeId;
        this.rating = rating;
        this.vicinity = vicinity;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Double getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(geometry, that.geometry) && Objects.equals(name, that.name) && Objects.equals(openingHours, that.openingHours) && Objects.equals(photos, that.photos) && Objects.equals(placeId, that.placeId) && Objects.equals(rating, that.rating) && Objects.equals(vicinity, that.vicinity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geometry, name, openingHours, photos, placeId, rating, vicinity);
    }

    @NonNull
    @Override
    public String toString() {
        return "Restaurant{" +
            "geometry=" + geometry +
            ", name='" + name + '\'' +
            ", openingHours=" + openingHours +
            ", photos=" + photos +
            ", placeId='" + placeId + '\'' +
            ", rating=" + rating +
            ", vicinity='" + vicinity + '\'' +
            '}';
    }
}
