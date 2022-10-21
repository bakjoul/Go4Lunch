package com.bakjoul.go4lunch.ui.map;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.restaurant.RestaurantMarker;

import java.util.List;
import java.util.Objects;

public class MapViewState {

   @NonNull
   private final List<RestaurantMarker> restaurantsMarkers;
   private final boolean isProgressBarVisible;

   public MapViewState(@NonNull List<RestaurantMarker> restaurantsMarkers, boolean isProgressBarVisible) {
      this.restaurantsMarkers = restaurantsMarkers;
      this.isProgressBarVisible = isProgressBarVisible;
   }

   @NonNull
   public List<RestaurantMarker> getRestaurantsMarkers() {
      return restaurantsMarkers;
   }

   public boolean isProgressBarVisible() {
      return isProgressBarVisible;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MapViewState that = (MapViewState) o;
      return isProgressBarVisible == that.isProgressBarVisible && restaurantsMarkers.equals(that.restaurantsMarkers);
   }

   @Override
   public int hashCode() {
      return Objects.hash(restaurantsMarkers, isProgressBarVisible);
   }

   @NonNull
   @Override
   public String toString() {
      return "MapViewState{" +
          "restaurantsMarkers=" + restaurantsMarkers +
          ", isProgressBarVisible=" + isProgressBarVisible +
          '}';
   }
}
