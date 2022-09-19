package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Restaurant {
    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("vicinity")
    @Expose
    private final String vicinity;

    @SerializedName("opening_hours")
    @Expose
    private final OpeningHours openingHours;

    @SerializedName("rating")
    @Expose
    private final Double rating;

    @SerializedName("photos")
    @Expose
    private final List<Photo> photos;

    @SerializedName("business_status")
    @Expose
    private final String business_status;

    public Restaurant(String placeId, String name, String vicinity, OpeningHours openingHours, Double rating, List<Photo> photos, String business_status) {
        this.placeId = placeId;
        this.name = name;
        this.vicinity = vicinity;
        this.openingHours = openingHours;
        this.rating = rating;
        this.photos = photos;
        this.business_status = business_status;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public Double getRating() {
        return rating;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getBusiness_status() {
        return business_status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(placeId, that.placeId) && Objects.equals(name, that.name) && Objects.equals(vicinity, that.vicinity) && Objects.equals(openingHours, that.openingHours) && Objects.equals(rating, that.rating) && Objects.equals(photos, that.photos) && Objects.equals(business_status, that.business_status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, vicinity, openingHours, rating, photos, business_status);
    }

    @NonNull
    @Override
    public String toString() {
        return "Restaurant{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", vicinity='" + vicinity + '\'' +
            ", openingHours=" + openingHours +
            ", rating=" + rating +
            ", photos=" + photos +
            ", business_status='" + business_status + '\'' +
            '}';
    }
}
