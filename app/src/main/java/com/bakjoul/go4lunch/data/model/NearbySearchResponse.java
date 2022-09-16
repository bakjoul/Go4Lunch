package com.bakjoul.go4lunch.data.model;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearchResponse {

    @SerializedName("results")
    @Expose
    private final List<Restaurant> results;
    @SerializedName("status")
    @Expose
    private final String status;

    public List<Restaurant> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public NearbySearchResponse(List<Restaurant> results, String status) {
        this.results = results;
        this.status = status;
    }
}

class Restaurant {
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
    private final List<Photo> photos = null;

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("rating")
    @Expose
    private final Double rating;

    @SerializedName("vicinity")
    @Expose
    private final String vicinity;

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

    public Restaurant(Geometry geometry, String name, OpeningHours openingHours, String placeId, Double rating, String vicinity) {
        this.geometry = geometry;
        this.name = name;
        this.openingHours = openingHours;
        this.placeId = placeId;
        this.rating = rating;
        this.vicinity = vicinity;
    }
}

class Geometry {
    @SerializedName("location")
    @Expose
    private final Location location;

    public Location getLocation() {
        return location;
    }

    public Geometry(Location location) {
        this.location = location;
    }
}

class OpeningHours {
    @SerializedName("open_now")
    @Expose
    private final Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    public OpeningHours(Boolean openNow) {
        this.openNow = openNow;
    }
}

class Photo {
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

    public Photo(Integer height, List<String> htmlAttributions, String photoReference, Integer width) {
        this.height = height;
        this.htmlAttributions = htmlAttributions;
        this.photoReference = photoReference;
        this.width = width;
    }
}