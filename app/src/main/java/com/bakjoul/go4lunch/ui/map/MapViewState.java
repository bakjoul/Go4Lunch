package com.bakjoul.go4lunch.ui.map;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

public class MapViewState {

   private final LatLng latLng;
   private final List<RestaurantMarker> restaurantsMarkers;

   public MapViewState(LatLng latLng, List<RestaurantMarker> restaurantsMarkers) {
      this.latLng = latLng;
      this.restaurantsMarkers = restaurantsMarkers;
   }

   public LatLng getLatLng() {
      return latLng;
   }

   public List<RestaurantMarker> getRestaurantsMarkers() {
      return restaurantsMarkers;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MapViewState that = (MapViewState) o;
      return Objects.equals(latLng, that.latLng) && Objects.equals(restaurantsMarkers, that.restaurantsMarkers);
   }

   @Override
   public int hashCode() {
      return Objects.hash(latLng, restaurantsMarkers);
   }

   @NonNull
   @Override
   public String toString() {
      return "MapViewState{" +
          "latLng=" + latLng +
          ", restaurantsMarkers=" + restaurantsMarkers +
          '}';
   }
}
