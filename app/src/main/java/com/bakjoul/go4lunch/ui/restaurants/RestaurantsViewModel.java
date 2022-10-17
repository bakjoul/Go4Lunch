package com.bakjoul.go4lunch.ui.restaurants;

import static com.bakjoul.go4lunch.data.restaurant.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.restaurant.RestaurantRepository.TYPE;

import android.app.Application;
import android.location.Location;
import android.util.Log;

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
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.GpsLocationRepository;
import com.bakjoul.go4lunch.data.repository.MapLocationRepository;
import com.bakjoul.go4lunch.data.restaurant.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantsViewModel extends ViewModel {

   private static final String TAG = "RestaurantsViewModel";

   private static final String BUSINESS_STATUS_OPERATIONAL = "OPERATIONAL";

   @NonNull
   private final Application application;

   @NonNull
   private final LocationDistanceUtil locationDistanceUtils;

   @NonNull
   private final RestaurantImageMapper restaurantImageMapper;

   private final MediatorLiveData<RestaurantsViewState> restaurantsViewStateMediatorLiveData = new MediatorLiveData<>();

   private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

   private final MutableLiveData<ErrorType> errorTypeMutableLiveData = new MutableLiveData<>();

   private final MutableLiveData<Boolean> isProgressBarVisibleLiveData = new MutableLiveData<>(true);

   @Inject
   public RestaurantsViewModel(
       @NonNull Application application,
       @NonNull RestaurantRepository restaurantRepository,
       @NonNull GpsLocationRepository gpsLocationRepository,
       @NonNull MapLocationRepository mapLocationRepository,
       @NonNull LocationDistanceUtil locationDistanceUtils,
       @NonNull RestaurantImageMapper restaurantImageMapper
   ) {
      this.application = application;
      this.locationDistanceUtils = locationDistanceUtils;
      this.restaurantImageMapper = restaurantImageMapper;

      LiveData<Location> gpsLocationLiveData = gpsLocationRepository.getCurrentLocation();
      LiveData<Location> mapLocationLiveData = mapLocationRepository.getCurrentMapLocation();
      LiveData<Boolean> isLocationGpsBasedMutableLiveData = gpsLocationRepository.getIsLocationGpsBasedMutableLiveData();

      LiveData<List<RestaurantsItemViewState>> restaurantsItemViewStateLiveData = Transformations.switchMap(
          isLocationGpsBasedMutableLiveData,
          isLocationGpsBased -> {
             if (isLocationGpsBased) {
                return Transformations.switchMap(
                    gpsLocationLiveData,
                    gpsLocation -> {
                       if (gpsLocation == null) {
                          isProgressBarVisibleLiveData.setValue(false);
                          return null;
                       }
                       return getRestaurantsListLiveData(gpsLocation, restaurantRepository);
                    }
                );
             } else {
                return Transformations.switchMap(
                    mapLocationLiveData,
                    mapLocation -> {
                       if (mapLocation == null) {
                          isProgressBarVisibleLiveData.setValue(false);
                          return null;
                       }
                       return getRestaurantsListLiveData(mapLocation, restaurantRepository);
                    }
                );
             }
          }
      );

      restaurantsViewStateMediatorLiveData.addSource(restaurantsItemViewStateLiveData, restaurantsItemViewStateList ->
          combine(restaurantsItemViewStateList, isProgressBarVisibleLiveData.getValue()));
      restaurantsViewStateMediatorLiveData.addSource(isProgressBarVisibleLiveData, isProgressBarVisible ->
          combine(restaurantsItemViewStateLiveData.getValue(), isProgressBarVisible));
   }

   @NonNull
   private LiveData<List<RestaurantsItemViewState>> getRestaurantsListLiveData(Location location, @NonNull RestaurantRepository restaurantRepository) {
      return Transformations.switchMap(
          nearbySearchRequestPingMutableLiveData,
          aVoid -> {
             errorTypeMutableLiveData.setValue(null);
             isProgressBarVisibleLiveData.setValue(true);
             return Transformations.map(
                 restaurantRepository.getNearbySearchResult(RestaurantsViewModel.this.getLocation(location), RANK_BY, TYPE, BuildConfig.MAPS_API_KEY),
                 result -> {
                    List<RestaurantsItemViewState> restaurantsItemViewStateList;
                    if (result.getResponse() != null) {
                       isProgressBarVisibleLiveData.setValue(false);
                       restaurantsItemViewStateList = mapData(result.getResponse(), location);
                    } else if (result.getErrorType() == ErrorType.TIMEOUT) {
                       isProgressBarVisibleLiveData.setValue(false);
                       restaurantsItemViewStateList = null;
                       errorTypeMutableLiveData.setValue(ErrorType.TIMEOUT);
                       Log.d(TAG, "Socket time out");
                    } else {
                       isProgressBarVisibleLiveData.setValue(false);
                       restaurantsItemViewStateList = null;
                    }
                    return restaurantsItemViewStateList;
                 }
             );
          }
      );
   }

   private void combine(
       @Nullable List<RestaurantsItemViewState> restaurantsItemViewStateList,
       @Nullable Boolean isProgressBarVisible) {
      restaurantsViewStateMediatorLiveData.setValue(
          new RestaurantsViewState(
              restaurantsItemViewStateList == null ? new ArrayList<>() : restaurantsItemViewStateList,
              restaurantsItemViewStateList == null || restaurantsItemViewStateList.isEmpty(),
              errorTypeMutableLiveData.getValue() == null ? null : errorTypeMutableLiveData.getValue(),
              Boolean.TRUE.equals(isProgressBarVisible)
          )
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

   public LiveData<RestaurantsViewState> getRestaurantsViewState() {
      return restaurantsViewStateMediatorLiveData;
   }

   public void onRetryButtonClicked() {
      nearbySearchRequestPingMutableLiveData.setValue(true);
   }

   public MutableLiveData<Boolean> getNearbySearchRequestPingMutableLiveData() {
      return nearbySearchRequestPingMutableLiveData;
   }
}
