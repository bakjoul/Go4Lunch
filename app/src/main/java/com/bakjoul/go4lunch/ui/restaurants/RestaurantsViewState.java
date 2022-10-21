package com.bakjoul.go4lunch.ui.restaurants;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class RestaurantsViewState {

   @NonNull
   private final List<RestaurantsItemViewState> restaurantsItemViewStates;
   private final boolean isEmptyStateVisible;
   private final boolean isProgressBarVisible;

   public RestaurantsViewState(@NonNull List<RestaurantsItemViewState> restaurantsItemViewStates, boolean isEmptyStateVisible, boolean isProgressBarVisible) {
      this.restaurantsItemViewStates = restaurantsItemViewStates;
      this.isEmptyStateVisible = isEmptyStateVisible;
      this.isProgressBarVisible = isProgressBarVisible;
   }

   @NonNull
   public List<RestaurantsItemViewState> getRestaurantsItemViewStates() {
      return restaurantsItemViewStates;
   }

   public boolean isEmptyStateVisible() {
      return isEmptyStateVisible;
   }

   public boolean isProgressBarVisible() {
      return isProgressBarVisible;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RestaurantsViewState that = (RestaurantsViewState) o;
      return isEmptyStateVisible == that.isEmptyStateVisible && isProgressBarVisible == that.isProgressBarVisible && restaurantsItemViewStates.equals(that.restaurantsItemViewStates);
   }

   @Override
   public int hashCode() {
      return Objects.hash(restaurantsItemViewStates, isEmptyStateVisible, isProgressBarVisible);
   }

   @NonNull
   @Override
   public String toString() {
      return "RestaurantsViewState{" +
          "restaurantsItemViewStates=" + restaurantsItemViewStates +
          ", isEmptyStateVisible=" + isEmptyStateVisible +
          ", isProgressBarVisible=" + isProgressBarVisible +
          '}';
   }
}
