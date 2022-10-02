package com.bakjoul.go4lunch.ui.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class DetailsViewState {

   @NonNull
   private final String id;

   @Nullable
   private final String photoUrl;

   @NonNull
   private final String name;

   private final float rating;

   private final boolean isRatingBarVisible;

   @NonNull
   private final String address;

   @NonNull
   private final String openingStatus;

   @NonNull
   private final String phoneNumber;

   @NonNull
   private final String websiteUrl;

   private final List<DetailsItemViewState> detailsItemViewStateList;

   public DetailsViewState(@NonNull String id, @Nullable String photoUrl, @NonNull String name, float rating, boolean isRatingBarVisible, @NonNull String address, @NonNull String openingStatus, @NonNull String phoneNumber, @NonNull String websiteUrl, List<DetailsItemViewState> detailsItemViewStateList) {
      this.id = id;
      this.photoUrl = photoUrl;
      this.name = name;
      this.rating = rating;
      this.isRatingBarVisible = isRatingBarVisible;
      this.address = address;
      this.openingStatus = openingStatus;
      this.phoneNumber = phoneNumber;
      this.websiteUrl = websiteUrl;
      this.detailsItemViewStateList = detailsItemViewStateList;
   }

   @NonNull
   public String getId() {
      return id;
   }

   @Nullable
   public String getPhotoUrl() {
      return photoUrl;
   }

   @NonNull
   public String getName() {
      return name;
   }

   public float getRating() {
      return rating;
   }

   public boolean isRatingBarVisible() {
      return isRatingBarVisible;
   }

   @NonNull
   public String getAddress() {
      return address;
   }

   @NonNull
   public String getOpeningStatus() {
      return openingStatus;
   }

   @NonNull
   public String getPhoneNumber() {
      return phoneNumber;
   }

   @NonNull
   public String getWebsiteUrl() {
      return websiteUrl;
   }

   public List<DetailsItemViewState> getDetailsItemViewStateList() {
      return detailsItemViewStateList;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DetailsViewState that = (DetailsViewState) o;
      return Float.compare(that.rating, rating) == 0 && isRatingBarVisible == that.isRatingBarVisible && id.equals(that.id) && Objects.equals(photoUrl, that.photoUrl) && name.equals(that.name) && address.equals(that.address) && openingStatus.equals(that.openingStatus) && phoneNumber.equals(that.phoneNumber) && websiteUrl.equals(that.websiteUrl) && Objects.equals(detailsItemViewStateList, that.detailsItemViewStateList);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, photoUrl, name, rating, isRatingBarVisible, address, openingStatus, phoneNumber, websiteUrl, detailsItemViewStateList);
   }

   @NonNull
   @Override
   public String toString() {
      return "DetailsViewState{" +
          "id='" + id + '\'' +
          ", photoUrl='" + photoUrl + '\'' +
          ", name='" + name + '\'' +
          ", rating=" + rating +
          ", isRatingBarVisible=" + isRatingBarVisible +
          ", address='" + address + '\'' +
          ", openingStatus='" + openingStatus + '\'' +
          ", phoneNumber='" + phoneNumber + '\'' +
          ", websiteUrl='" + websiteUrl + '\'' +
          ", detailsItemViewStateList=" + detailsItemViewStateList +
          '}';
   }
}
