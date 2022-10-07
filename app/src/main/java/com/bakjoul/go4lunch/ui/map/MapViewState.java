package com.bakjoul.go4lunch.ui.map;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

public class MapViewState {

   private final LatLng latLng;
   private final List<RestaurantMarker> restaurantsMarkers;
   private final boolean isProgressBarVisible;

   public MapViewState(LatLng latLng, List<RestaurantMarker> restaurantsMarkers, boolean isProgressBarVisible) {
      this.latLng = latLng;
      this.restaurantsMarkers = restaurantsMarkers;
      this.isProgressBarVisible = isProgressBarVisible;
   }

   public LatLng getLatLng() {
      return latLng;
   }

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
      return isProgressBarVisible == that.isProgressBarVisible && Objects.equals(latLng, that.latLng) && Objects.equals(restaurantsMarkers, that.restaurantsMarkers);
   }

   @Override
   public int hashCode() {
      return Objects.hash(latLng, restaurantsMarkers, isProgressBarVisible);
   }

   @NonNull
   @Override
   public String toString() {
      return "MapViewState{" +
          "latLng=" + latLng +
          ", restaurantsMarkers=" + restaurantsMarkers +
          ", isProgressBarVisible=" + isProgressBarVisible +
          '}';
   }
}
