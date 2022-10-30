package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;

import java.util.Objects;

public class WorkmateLikedRestaurant {
   private String restaurantId;
   private String restaurantName;

   public WorkmateLikedRestaurant() {
   }

   public WorkmateLikedRestaurant(String restaurantId, String restaurantName) {
      this.restaurantId = restaurantId;
      this.restaurantName = restaurantName;
   }

   public String getRestaurantId() {
      return restaurantId;
   }

   public String getRestaurantName() {
      return restaurantName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      WorkmateLikedRestaurant that = (WorkmateLikedRestaurant) o;
      return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(restaurantName, that.restaurantName);
   }

   @Override
   public int hashCode() {
      return Objects.hash(restaurantId, restaurantName);
   }

   @NonNull
   @Override
   public String toString() {
      return "WorkmateLikedRestaurant{" +
          "restaurantId='" + restaurantId + '\'' +
          ", restaurantName='" + restaurantName + '\'' +
          '}';
   }
}
