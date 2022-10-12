package com.bakjoul.go4lunch.ui.restaurants;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.model.ErrorType;

import java.util.List;
import java.util.Objects;

public class RestaurantsViewState {

   private final List<RestaurantsItemViewState> restaurantsItemViewStates;
   private final boolean isEmptyStateVisible;
   private final ErrorType errorType;
   private final boolean isProgressBarVisible;

   public RestaurantsViewState(List<RestaurantsItemViewState> restaurantsItemViewStates, boolean isEmptyStateVisible, ErrorType errorType, boolean isProgressBarVisible) {
      this.restaurantsItemViewStates = restaurantsItemViewStates;
      this.isEmptyStateVisible = isEmptyStateVisible;
      this.errorType = errorType;
      this.isProgressBarVisible = isProgressBarVisible;
   }

   public List<RestaurantsItemViewState> getRestaurantsItemViewStates() {
      return restaurantsItemViewStates;
   }

   public boolean isEmptyStateVisible() {
      return isEmptyStateVisible;
   }

   public ErrorType getErrorType() {
      return errorType;
   }

   public boolean isProgressBarVisible() {
      return isProgressBarVisible;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RestaurantsViewState that = (RestaurantsViewState) o;
      return isEmptyStateVisible == that.isEmptyStateVisible && isProgressBarVisible == that.isProgressBarVisible && Objects.equals(restaurantsItemViewStates, that.restaurantsItemViewStates) && errorType == that.errorType;
   }

   @Override
   public int hashCode() {
      return Objects.hash(restaurantsItemViewStates, isEmptyStateVisible, errorType, isProgressBarVisible);
   }

   @NonNull
   @Override
   public String toString() {
      return "RestaurantsViewState{" +
          "restaurantsItemViewStates=" + restaurantsItemViewStates +
          ", isEmptyStateVisible=" + isEmptyStateVisible +
          ", errorType=" + errorType +
          ", isProgressBarVisible=" + isProgressBarVisible +
          '}';
   }
}
