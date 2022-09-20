package com.bakjoul.go4lunch.ui.utils;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.bakjoul.go4lunch.BuildConfig;

import javax.inject.Inject;

public class RestaurantImageMapper {

    @Inject
    public RestaurantImageMapper() {

    }

    @Nullable
    public String getImageUrl(String photoRef) {
        return new Uri.Builder()
            .scheme("https")
            .authority("maps.googleapis.com")
            .path("/maps/api/place/photo")
            .appendQueryParameter("maxwidth", "100")
            .appendQueryParameter("photoreference", photoRef)
            .appendQueryParameter("key", BuildConfig.MAPS_API_KEY)
            .toString();
    }
}
