package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class WorkmateData {
   private WorkmateChosenRestaurant workmateChosenRestaurant;
   private List<WorkmateLikedRestaurant> workmateLikedRestaurantList;

   public WorkmateData() {
   }

   public WorkmateData(WorkmateChosenRestaurant workmateChosenRestaurant, List<WorkmateLikedRestaurant> workmateLikedRestaurantList) {
      this.workmateChosenRestaurant = workmateChosenRestaurant;
      this.workmateLikedRestaurantList = workmateLikedRestaurantList;
   }

   public WorkmateChosenRestaurant getWorkmateChosenRestaurant() {
      return workmateChosenRestaurant;
   }

   public List<WorkmateLikedRestaurant> getWorkmateLikedRestaurantList() {
      return workmateLikedRestaurantList;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      WorkmateData that = (WorkmateData) o;
      return Objects.equals(workmateChosenRestaurant, that.workmateChosenRestaurant) && Objects.equals(workmateLikedRestaurantList, that.workmateLikedRestaurantList);
   }

   @Override
   public int hashCode() {
      return Objects.hash(workmateChosenRestaurant, workmateLikedRestaurantList);
   }

   @NonNull
   @Override
   public String toString() {
      return "WorkmateData{" +
          "workmateChosenRestaurant=" + workmateChosenRestaurant +
          ", workmateLikedRestaurantList=" + workmateLikedRestaurantList +
          '}';
   }
}
