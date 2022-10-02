package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class RestaurantDetailsResponse {
   @SerializedName("place_id")
   @Expose
   private final String placeId;

   @SerializedName("name")
   @Expose
   private final String name;

   @SerializedName("rating")
   @Expose
   private final double rating;

   @SerializedName("user_ratings_total")
   @Expose
   private final int userRatingsTotal;

   @SerializedName("formatted_address")
   @Expose
   private final String formattedAddress;

   @SerializedName("opening_hours")
   @Expose
   private final OpeningHoursResponse openingHoursResponse;

   @SerializedName("photos")
   @Expose
   private final List<PhotoResponse> photoResponses;

   @SerializedName("formatted_phone_number")
   @Expose
   private final String formattedPhoneNumber;

   @SerializedName("website")
   @Expose
   private final String website;

   public RestaurantDetailsResponse(String placeId, String name, double rating, int userRatingsTotal, String formattedAddress, OpeningHoursResponse openingHoursResponse, List<PhotoResponse> photoResponses, String formattedPhoneNumber, String website) {
      this.placeId = placeId;
      this.name = name;
      this.rating = rating;
      this.userRatingsTotal = userRatingsTotal;
      this.formattedAddress = formattedAddress;
      this.openingHoursResponse = openingHoursResponse;
      this.photoResponses = photoResponses;
      this.formattedPhoneNumber = formattedPhoneNumber;
      this.website = website;
   }

   public String getPlaceId() {
      return placeId;
   }

   public String getName() {
      return name;
   }

   public double getRating() {
      return rating;
   }

   public int getUserRatingsTotal() {
      return userRatingsTotal;
   }

   public String getFormattedAddress() {
      return formattedAddress;
   }

   public OpeningHoursResponse getOpeningHoursResponse() {
      return openingHoursResponse;
   }

   public List<PhotoResponse> getPhotoResponses() {
      return photoResponses;
   }

   public String getFormattedPhoneNumber() {
      return formattedPhoneNumber;
   }

   public String getWebsite() {
      return website;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RestaurantDetailsResponse that = (RestaurantDetailsResponse) o;
      return Double.compare(that.rating, rating) == 0 && userRatingsTotal == that.userRatingsTotal && Objects.equals(placeId, that.placeId) && Objects.equals(name, that.name) && Objects.equals(formattedAddress, that.formattedAddress) && Objects.equals(openingHoursResponse, that.openingHoursResponse) && Objects.equals(photoResponses, that.photoResponses) && Objects.equals(formattedPhoneNumber, that.formattedPhoneNumber) && Objects.equals(website, that.website);
   }

   @Override
   public int hashCode() {
      return Objects.hash(placeId, name, rating, userRatingsTotal, formattedAddress, openingHoursResponse, photoResponses, formattedPhoneNumber, website);
   }

   @NonNull
   @Override
   public String toString() {
      return "RestaurantDetailsResponse{" +
          "placeId='" + placeId + '\'' +
          ", name='" + name + '\'' +
          ", rating=" + rating +
          ", userRatingsTotal=" + userRatingsTotal +
          ", formattedAddress='" + formattedAddress + '\'' +
          ", openingHoursResponse=" + openingHoursResponse +
          ", photoResponses=" + photoResponses +
          ", formattedPhoneNumber='" + formattedPhoneNumber + '\'' +
          ", website='" + website + '\'' +
          '}';
   }
}
