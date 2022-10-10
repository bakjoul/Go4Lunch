package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class NearbySearchResult {
   @Nullable
   private final NearbySearchResponse response;

   @Nullable
   private final ErrorType errorType;

   public NearbySearchResult(@Nullable NearbySearchResponse response, @Nullable ErrorType errorType) {
      this.response = response;
      this.errorType = errorType;
   }

   @Nullable
   public NearbySearchResponse getResponse() {
      return response;
   }

   @Nullable
   public ErrorType getErrorType() {
      return errorType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      NearbySearchResult that = (NearbySearchResult) o;
      return Objects.equals(response, that.response) && errorType == that.errorType;
   }

   @Override
   public int hashCode() {
      return Objects.hash(response, errorType);
   }

   @NonNull
   @Override
   public String toString() {
      return "RestaurantResult{" +
          "response=" + response +
          ", errorType=" + errorType +
          '}';
   }
}
