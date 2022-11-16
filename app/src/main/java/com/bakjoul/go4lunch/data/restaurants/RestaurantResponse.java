package com.bakjoul.go4lunch.data.restaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bakjoul.go4lunch.data.model.GeometryResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class RestaurantResponse {
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
    private final OpeningHoursResponse openingHoursResponse;

    @SerializedName("geometry")
    @Expose
    private final GeometryResponse geometryResponse;

    @SerializedName("rating")
    @Expose
    private final double rating;

    @SerializedName("photos")
    @Expose
    private final List<PhotoResponse> photoResponses;

    @SerializedName("business_status")
    @Expose
    private final String businessStatus;

    @SerializedName("user_ratings_total")
    @Expose
    private final int userRatingsTotal;

    public RestaurantResponse(String placeId, String name, String vicinity, OpeningHoursResponse openingHoursResponse, GeometryResponse geometryResponse, double rating, List<PhotoResponse> photoResponses, String businessStatus, int userRatingsTotal) {
        this.placeId = placeId;
        this.name = name;
        this.vicinity = vicinity;
        this.openingHoursResponse = openingHoursResponse;
        this.geometryResponse = geometryResponse;
        this.rating = rating;
        this.photoResponses = photoResponses;
        this.businessStatus = businessStatus;
        this.userRatingsTotal = userRatingsTotal;
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

    @Nullable
    public OpeningHoursResponse getOpeningHours() {
        return openingHoursResponse;
    }

    public GeometryResponse getGeometry() {
        return geometryResponse;
    }

    public double getRating() {
        return rating;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public List<PhotoResponse> getPhotos() {
        return photoResponses;
    }

    @Nullable
    public String getBusinessStatus() {
        return businessStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantResponse that = (RestaurantResponse) o;
        return Double.compare(that.rating, rating) == 0 && userRatingsTotal == that.userRatingsTotal && Objects.equals(placeId, that.placeId) && Objects.equals(name, that.name) && Objects.equals(vicinity, that.vicinity) && Objects.equals(openingHoursResponse, that.openingHoursResponse) && Objects.equals(geometryResponse, that.geometryResponse) && Objects.equals(photoResponses, that.photoResponses) && Objects.equals(businessStatus, that.businessStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, name, vicinity, openingHoursResponse, geometryResponse, rating, photoResponses, businessStatus, userRatingsTotal);
    }

    @NonNull
    @Override
    public String toString() {
        return "Restaurant{" +
            "placeId='" + placeId + '\'' +
            ", name='" + name + '\'' +
            ", vicinity='" + vicinity + '\'' +
            ", openingHours=" + openingHoursResponse +
            ", geometry=" + geometryResponse +
            ", rating=" + rating +
            ", photos=" + photoResponses +
            ", business_status='" + businessStatus + '\'' +
            ", user_ratings_total='" + userRatingsTotal + '\'' +
            '}';
    }
}
