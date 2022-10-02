package com.bakjoul.go4lunch.ui.restaurants;

import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.TYPE;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantsViewModel extends ViewModel {

   private static final String BUSINESS_STATUS_OPERATIONAL = "OPERATIONAL";

   @NonNull
   private final Application application;

   @NonNull
   private final LocationDistanceUtil locationDistanceUtils;

   @NonNull
   private final RestaurantImageMapper restaurantImageMapper;

   private final LiveData<RestaurantsViewState> restaurantsViewState;

   @Inject
   public RestaurantsViewModel(
       @NonNull Application application,
       @NonNull RestaurantRepository restaurantRepository,
       @NonNull LocationRepository locationRepository,
       @NonNull LocationDistanceUtil locationDistanceUtils,
       @NonNull RestaurantImageMapper restaurantImageMapper
   ) {
      this.application = application;
      this.locationDistanceUtils = locationDistanceUtils;
      this.restaurantImageMapper = restaurantImageMapper;

      restaurantsViewState = Transformations.switchMap(
          locationRepository.getCurrentLocation(), location -> {
             if (location != null) {
                LiveData<NearbySearchResponse> nearbySearchResponseLiveData = restaurantRepository.getNearbySearchResponse(
                    RestaurantsViewModel.this.getLocation(location),
                    RANK_BY,
                    TYPE,
                    BuildConfig.MAPS_API_KEY
                );
                return Transformations.map(
                    nearbySearchResponseLiveData,
                    response -> {
                       List<RestaurantsItemViewState> restaurantsItemViewStateList;
                       if (response != null) {
                          restaurantsItemViewStateList = RestaurantsViewModel.this.mapData(response, location);
                       } else {
                          restaurantsItemViewStateList = new ArrayList<>();
                       }
                       return new RestaurantsViewState(restaurantsItemViewStateList, restaurantsItemViewStateList.isEmpty());
                    }
                );
             } else {
                return new MutableLiveData<>(new RestaurantsViewState(new ArrayList<>(), true));
             }
          }
      );
   }

   @NonNull
   private List<RestaurantsItemViewState> mapData(
       @NonNull NearbySearchResponse response,
       Location location
   ) {
      List<RestaurantsItemViewState> restaurantsItemViewStateList = new ArrayList<>();

      for (RestaurantResponse r : response.getResults()) {
         if (BUSINESS_STATUS_OPERATIONAL.equals(r.getBusinessStatus())) {
            restaurantsItemViewStateList.add(
                new RestaurantsItemViewState(
                    r.getPlaceId(),
                    r.getName(),
                    r.getVicinity(),
                    isOpen(r.getOpeningHours()),
                    getDistance(location, r.getGeometry().getLocation()),
                    "",
                    getRating(r.getRating()),
                    isRatingBarVisible(r.getUserRatingsTotal()),
                    getPhotoUrl(r.getPhotos())
                )
            );
         }
      }

      return restaurantsItemViewStateList;
   }

   @NonNull
   private String getLocation(@NonNull Location location) {
      return location.getLatitude() + "," + location.getLongitude();
   }

   @NonNull
   private String isOpen(OpeningHoursResponse response) {
      String isOpen;
      if (response != null) {
         if (response.getOpenNow()) {
            isOpen = application.getString(R.string.restaurant_item_is_open);
         } else {
            isOpen = application.getString(R.string.restaurant_item_is_closed);
         }
      } else {
         isOpen = application.getString(R.string.restaurant_item_info_not_available);
      }
      return isOpen;
   }

   @NonNull
   private String getDistance(@NonNull Location currentLocation, @NonNull LocationResponse restaurantLocationResponse) {
      return locationDistanceUtils.getDistance(currentLocation, restaurantLocationResponse);
   }

   private float getRating(double restaurantRating) {
      return (float) Math.round(((restaurantRating * 3 / 5) / 0.5) * 0.5);
   }

   private boolean isRatingBarVisible(int userRatingsTotal) {
      return userRatingsTotal > 0;
   }

   @Nullable
   private String getPhotoUrl(List<PhotoResponse> photoResponses) {
      if (photoResponses != null) {
         String photoRef = photoResponses.get(0).getPhotoReference();
         if (photoRef != null) {
            return restaurantImageMapper.getImageUrl(photoRef, false);
         }
      }
      return null;
   }

   public LiveData<RestaurantsViewState> getRestaurantsViewState() {
      return restaurantsViewState;
   }
}
