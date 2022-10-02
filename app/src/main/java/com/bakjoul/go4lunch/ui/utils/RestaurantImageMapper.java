package com.bakjoul.go4lunch.ui.utils;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.bakjoul.go4lunch.BuildConfig;

import javax.inject.Inject;

public class RestaurantImageMapper {

   private static final String SMALL = "80";
   private static final String LARGE = "400";

   @Inject
   public RestaurantImageMapper() {

   }

   @Nullable
   public String getImageUrl(String photoRef, boolean isForLargeSize) {
      String width = SMALL;
      if (isForLargeSize) {
         width = LARGE;
      }
      return new Uri.Builder()
          .scheme("https")
          .authority("maps.googleapis.com")
          .path("/maps/api/place/photo")
          .appendQueryParameter("maxwidth", width)
          .appendQueryParameter("photoreference", photoRef)
          .appendQueryParameter("key", BuildConfig.MAPS_API_KEY)
          .toString();
   }
}
