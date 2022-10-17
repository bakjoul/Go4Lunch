package com.bakjoul.go4lunch.ui.utils;

import android.location.Location;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Locale;

import javax.inject.Inject;

public class LocationDistanceUtil {

   @Inject
   public LocationDistanceUtil() {
   }

   public String getDistanceToStringFormat(@NonNull Location currentLocation, @NonNull LocationResponse restaurantLocationResponse) {
      return String.format(
          Locale.getDefault(),
          "%.0fm",
          SphericalUtil.computeDistanceBetween(
              new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
              new LatLng(restaurantLocationResponse.getLat(), restaurantLocationResponse.getLng())
          )
      );
   }

   public double getDistance(@NonNull LatLng newPosition, @NonNull LatLng oldPosition) {
      return SphericalUtil.computeDistanceBetween(newPosition, oldPosition);
   }
}
