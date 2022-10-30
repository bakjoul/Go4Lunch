package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class WorkmateData {
   private String chosenRestaurantId;
   private List<String> likedRestaurantsIds;

   public WorkmateData() {
   }

   public WorkmateData(String chosenRestaurantId, List<String> likedRestaurantsIds) {
      this.chosenRestaurantId = chosenRestaurantId;
      this.likedRestaurantsIds = likedRestaurantsIds;
   }

   public String getChosenRestaurantId() {
      return chosenRestaurantId;
   }

   public List<String> getLikedRestaurantsIds() {
      return likedRestaurantsIds;
   }

   public void setLikedRestaurantsIds(List<String> likedRestaurantsIds) {
      this.likedRestaurantsIds = likedRestaurantsIds;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      WorkmateData that = (WorkmateData) o;
      return Objects.equals(chosenRestaurantId, that.chosenRestaurantId) && Objects.equals(likedRestaurantsIds, that.likedRestaurantsIds);
   }

   @Override
   public int hashCode() {
      return Objects.hash(chosenRestaurantId, likedRestaurantsIds);
   }

   @NonNull
   @Override
   public String toString() {
      return "WorkmateData{" +
          "chosenRestaurantId='" + chosenRestaurantId + '\'' +
          ", likedRestaurantsIds=" + likedRestaurantsIds +
          '}';
   }
}
