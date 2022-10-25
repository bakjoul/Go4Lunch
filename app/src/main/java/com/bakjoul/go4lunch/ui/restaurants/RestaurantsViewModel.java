package com.bakjoul.go4lunch.ui.restaurants;

import static com.bakjoul.go4lunch.data.restaurants.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.restaurants.RestaurantRepository.TYPE;

import android.app.Application;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.location.GpsLocationRepository;
import com.bakjoul.go4lunch.data.location.LocationModeRepository;
import com.bakjoul.go4lunch.data.location.MapLocationRepository;
import com.bakjoul.go4lunch.data.restaurants.RestaurantRepository;
import com.bakjoul.go4lunch.data.restaurants.RestaurantResponse;
import com.bakjoul.go4lunch.data.restaurants.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;

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

   private Location currentLocation = new Location(LocationManager.GPS_PROVIDER);

   private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

   private final SingleLiveEvent<Boolean> isRetryBarVisibleSingleLiveEvent = new SingleLiveEvent<>();

   private final MediatorLiveData<RestaurantsViewState> restaurantsViewStateMediatorLiveData = new MediatorLiveData<>();

   @Inject
   public RestaurantsViewModel(
       @NonNull Application application,
       @NonNull GpsLocationRepository gpsLocationRepository,
       @NonNull MapLocationRepository mapLocationRepository,
       @NonNull LocationModeRepository locationModeRepository,
       @NonNull RestaurantRepository restaurantRepository,
       @NonNull LocationDistanceUtil locationDistanceUtils,
       @NonNull RestaurantImageMapper restaurantImageMapper
   ) {
      this.application = application;
      this.locationDistanceUtils = locationDistanceUtils;
      this.restaurantImageMapper = restaurantImageMapper;

      LiveData<Boolean> isUserModeEnabledLiveData = locationModeRepository.isUserModeEnabledLiveData();

      LiveData<Location> locationLiveData = Transformations.switchMap(
          isUserModeEnabledLiveData,
          isUserModeEnabled -> {
             if (isUserModeEnabled) {
                return mapLocationRepository.getCurrentMapLocationLiveData();
             } else {
                return gpsLocationRepository.getCurrentLocationLiveData();
             }
          }
      );

      LiveData<RestaurantResponseWrapper> responseWrapperLiveData = Transformations.switchMap(
          locationLiveData,
          location -> {
             if (location == null) {
                return new MutableLiveData<>(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.LOCATION_NULL));
             }
             currentLocation = location;
             return Transformations.switchMap(
                 nearbySearchRequestPingMutableLiveData,
                 aVoid -> restaurantRepository.getNearbySearchResponse(getLocation(location), RANK_BY, TYPE, BuildConfig.MAPS_API_KEY)
             );
          }
      );

      restaurantsViewStateMediatorLiveData.addSource(responseWrapperLiveData, responseWrapper ->
          combine(responseWrapper));
   }

   private void combine(@Nullable RestaurantResponseWrapper restaurantResponseWrapper) {
      if (restaurantResponseWrapper == null) {
         return;
      }

      List<RestaurantsItemViewState> restaurantsItemViewStates = new ArrayList<>();
      boolean isProgressBarVisible = restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.LOADING;
      isRetryBarVisibleSingleLiveEvent.setValue(false);

      if (restaurantResponseWrapper.getNearbySearchResponse() != null
          && restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.SUCCESS) {

         isProgressBarVisible = false;

         map(restaurantResponseWrapper, restaurantsItemViewStates);
      }

      if (restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.IO_ERROR
          || restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.CRITICAL_ERROR) {
         isRetryBarVisibleSingleLiveEvent.setValue(true);
      }

      restaurantsViewStateMediatorLiveData.setValue(
          new RestaurantsViewState(
              restaurantsItemViewStates,
              restaurantsItemViewStates.isEmpty(),
              isProgressBarVisible
          )
      );

   }

   private void map(@NonNull RestaurantResponseWrapper restaurantResponseWrapper, List<RestaurantsItemViewState> restaurantsItemViewStates) {
      if (restaurantResponseWrapper.getNearbySearchResponse() != null) {
         for (RestaurantResponse r : restaurantResponseWrapper.getNearbySearchResponse().getResults()) {
            if (BUSINESS_STATUS_OPERATIONAL.equals(r.getBusinessStatus())) {
               restaurantsItemViewStates.add(
                   new RestaurantsItemViewState(
                       r.getPlaceId(),
                       r.getName(),
                       r.getVicinity(),
                       isOpen(r.getOpeningHours()),
                       getDistance(currentLocation, r.getGeometry().getLocation()),
                       "",
                       getRating(r.getRating()),
                       isRatingBarVisible(r.getUserRatingsTotal()),
                       getPhotoUrl(r.getPhotos())
                   )
               );
            }
         }
      }
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
            isOpen = application.getString(R.string.restaurant_is_open);
         } else {
            isOpen = application.getString(R.string.restaurant_is_closed);
         }
      } else {
         isOpen = application.getString(R.string.information_not_available);
      }
      return isOpen;
   }

   @NonNull
   private String getDistance(@NonNull Location currentLocation, @NonNull LocationResponse restaurantLocationResponse) {
      return locationDistanceUtils.getDistanceToStringFormat(currentLocation, restaurantLocationResponse);
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

   public SingleLiveEvent<Boolean> getIsRetryBarVisibleSingleLiveEvent() {
      return isRetryBarVisibleSingleLiveEvent;
   }

   public LiveData<RestaurantsViewState> getRestaurantsViewState() {
      return restaurantsViewStateMediatorLiveData;
   }

   public void onRetryButtonClicked() {
      nearbySearchRequestPingMutableLiveData.setValue(true);
   }

   public LiveData<Boolean> getNearbySearchRequestPingMutableLiveData() {
      return nearbySearchRequestPingMutableLiveData;
   }
}
