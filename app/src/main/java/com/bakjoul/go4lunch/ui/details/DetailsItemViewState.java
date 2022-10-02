package com.bakjoul.go4lunch.ui.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class DetailsItemViewState {

   @NonNull
   private final String userId;

   @Nullable
   private final String userPhotoUrl;

   private final String message;

   public DetailsItemViewState(@NonNull String userId, @Nullable String userPhotoUrl, String message) {
      this.userId = userId;
      this.userPhotoUrl = userPhotoUrl;
      this.message = message;
   }

   @NonNull
   public String getUserId() {
      return userId;
   }

   @Nullable
   public String getUserPhotoUrl() {
      return userPhotoUrl;
   }

   public String getMessage() {
      return message;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DetailsItemViewState that = (DetailsItemViewState) o;
      return userId.equals(that.userId) && Objects.equals(userPhotoUrl, that.userPhotoUrl) && Objects.equals(message, that.message);
   }

   @Override
   public int hashCode() {
      return Objects.hash(userId, userPhotoUrl, message);
   }

   @NonNull
   @Override
   public String toString() {
      return "DetailsItemViewState{" +
          "userId='" + userId + '\'' +
          ", userPhotoUrl='" + userPhotoUrl + '\'' +
          ", message='" + message + '\'' +
          '}';
   }
}
