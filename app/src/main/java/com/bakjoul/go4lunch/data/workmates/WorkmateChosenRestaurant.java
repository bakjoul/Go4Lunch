package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;

import java.util.Objects;

public class WorkmateChosenRestaurant {
   private String restaurantId;
   private String restaurantName;

   public WorkmateChosenRestaurant() {
   }

   public WorkmateChosenRestaurant(String restaurantId, String restaurantName) {
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
      WorkmateChosenRestaurant that = (WorkmateChosenRestaurant) o;
      return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(restaurantName, that.restaurantName);
   }

   @Override
   public int hashCode() {
      return Objects.hash(restaurantId, restaurantName);
   }

   @NonNull
   @Override
   public String toString() {
      return "ChosenRestaurant{" +
          "restaurantId='" + restaurantId + '\'' +
          ", restaurantName='" + restaurantName + '\'' +
          '}';
   }
}
