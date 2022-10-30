package com.bakjoul.go4lunch.ui.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class DetailsViewState {

   @Nullable
   private final String id;

   @Nullable
   private final String photoUrl;

   @Nullable
   private final String name;

   private final float rating;

   private final boolean isRatingBarVisible;

   @Nullable
   private final String address;

   @Nullable
   private final String openingStatus;

   @Nullable
   private final String phoneNumber;

   @Nullable
   private final String websiteUrl;

   private final boolean isLiked;

   private final boolean isChosen;

   private final boolean isProgressBarVisible;

   private final List<DetailsItemViewState> detailsItemViewStateList;

   public DetailsViewState(@Nullable String id, @Nullable String photoUrl, @Nullable String name, float rating, boolean isRatingBarVisible, @Nullable String address, @Nullable String openingStatus, @Nullable String phoneNumber, @Nullable String websiteUrl, boolean isLiked, boolean isChosen, boolean isProgressBarVisible, List<DetailsItemViewState> detailsItemViewStateList) {
      this.id = id;
      this.photoUrl = photoUrl;
      this.name = name;
      this.rating = rating;
      this.isRatingBarVisible = isRatingBarVisible;
      this.address = address;
      this.openingStatus = openingStatus;
      this.phoneNumber = phoneNumber;
      this.websiteUrl = websiteUrl;
      this.isLiked = isLiked;
      this.isChosen = isChosen;
      this.isProgressBarVisible = isProgressBarVisible;
      this.detailsItemViewStateList = detailsItemViewStateList;
   }

   @Nullable
   public String getId() {
      return id;
   }

   @Nullable
   public String getPhotoUrl() {
      return photoUrl;
   }

   @Nullable
   public String getName() {
      return name;
   }

   public float getRating() {
      return rating;
   }

   public boolean isRatingBarVisible() {
      return isRatingBarVisible;
   }

   @Nullable
   public String getAddress() {
      return address;
   }

   @Nullable
   public String getOpeningStatus() {
      return openingStatus;
   }

   @Nullable
   public String getPhoneNumber() {
      return phoneNumber;
   }

   @Nullable
   public String getWebsiteUrl() {
      return websiteUrl;
   }

   public boolean isLiked() {
      return isLiked;
   }

   public boolean isChosen() {
      return isChosen;
   }

   public boolean isProgressBarVisible() {
      return isProgressBarVisible;
   }

   public List<DetailsItemViewState> getDetailsItemViewStateList() {
      return detailsItemViewStateList;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DetailsViewState that = (DetailsViewState) o;
      return Float.compare(that.rating, rating) == 0 && isRatingBarVisible == that.isRatingBarVisible && isLiked == that.isLiked && isChosen == that.isChosen && isProgressBarVisible == that.isProgressBarVisible && Objects.equals(id, that.id) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(name, that.name) && Objects.equals(address, that.address) && Objects.equals(openingStatus, that.openingStatus) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(websiteUrl, that.websiteUrl) && Objects.equals(detailsItemViewStateList, that.detailsItemViewStateList);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, photoUrl, name, rating, isRatingBarVisible, address, openingStatus, phoneNumber, websiteUrl, isLiked, isChosen, isProgressBarVisible, detailsItemViewStateList);
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
          ", isLiked=" + isLiked +
          ", isChosen=" + isChosen +
          ", isProgressBarVisible=" + isProgressBarVisible +
          ", detailsItemViewStateList=" + detailsItemViewStateList +
          '}';
   }
}
