package com.bakjoul.go4lunch.ui.map;

import static com.bakjoul.go4lunch.data.restaurant.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.restaurant.RestaurantRepository.TYPE;

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
import com.bakjoul.go4lunch.data.repository.GpsLocationRepository;
import com.bakjoul.go4lunch.data.repository.GpsModeRepository;
import com.bakjoul.go4lunch.data.repository.MapLocationRepository;
import com.bakjoul.go4lunch.data.restaurant.RestaurantMarker;
import com.bakjoul.go4lunch.data.restaurant.RestaurantRepository;
import com.bakjoul.go4lunch.data.restaurant.RestaurantResponse;
import com.bakjoul.go4lunch.data.restaurant.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

   private static final double MAP_MINIMUM_DISPLACEMENT = 1000;

   @NonNull
   private final MapLocationRepository mapLocationRepository;

   @NonNull
   private final GpsModeRepository gpsModeRepository;

   @NonNull
   private final LocationDistanceUtil locationDistanceUtil;

   private LatLng currentLocation = null;

   private LatLng lastLocation = null;

   private final MutableLiveData<Boolean> isMapReadyMutableLiveData = new MutableLiveData<>(false);

   private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

   private final SingleLiveEvent<LatLng> cameraSingleLiveEvent = new SingleLiveEvent<>();

   private final SingleLiveEvent<Boolean> isRetryBarVisibleSingleLiveEvent = new SingleLiveEvent<>();

   private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

   @Inject
   public MapViewModel(
       @NonNull GpsLocationRepository gpsLocationRepository,
       @NonNull MapLocationRepository mapLocationRepository,
       @NonNull GpsModeRepository gpsModeRepository,
       @NonNull RestaurantRepository restaurantRepository,
       @NonNull LocationDistanceUtil locationDistanceUtil
   ) {
      this.mapLocationRepository = mapLocationRepository;
      this.gpsModeRepository = gpsModeRepository;
      this.locationDistanceUtil = locationDistanceUtil;

      LiveData<Boolean> isUserModeEnabledLiveData = gpsModeRepository.isUserModeEnabledLiveData();

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

      cameraSingleLiveEvent.addSource(locationLiveData, location -> {
         if (location != null) {
            cameraSingleLiveEvent.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
         }
      });

      LiveData<RestaurantResponseWrapper> responseWrapperLiveData = Transformations.switchMap(
          locationLiveData,
          location -> {
             if (location == null) {
                return new MutableLiveData<>(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.LOCATION_NULL));
             }
             cameraSingleLiveEvent.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
             currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
             return Transformations.switchMap(
                 nearbySearchRequestPingMutableLiveData,
                 aVoid -> restaurantRepository.getNearbySearchResponse(getLocation(location), RANK_BY, TYPE, BuildConfig.MAPS_API_KEY)
             );
          }
      );

      mapViewStateMediatorLiveData.addSource(isMapReadyMutableLiveData, isMapReady ->
          combine(isMapReady, responseWrapperLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(responseWrapperLiveData, responseWrapper ->
          combine(isMapReadyMutableLiveData.getValue(), responseWrapper)
      );
   }

   private void combine(
       @Nullable Boolean isMapReady,
       @Nullable RestaurantResponseWrapper restaurantResponseWrapper
   ) {
      if (isMapReady == null || restaurantResponseWrapper == null) {
         return;
      }

      List<RestaurantMarker> restaurantsMarkers = new ArrayList<>();
      boolean isProgressBarVisible = restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.LOADING;
      isRetryBarVisibleSingleLiveEvent.setValue(false);

      if (restaurantResponseWrapper.getNearbySearchResponse() != null
          && restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.SUCCESS) {

         isProgressBarVisible = false;

         map(restaurantResponseWrapper, restaurantsMarkers);

         // Updates last know location
         lastLocation = currentLocation;
      }

      if (restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.IO_ERROR
          || restaurantResponseWrapper.getState() == RestaurantResponseWrapper.State.CRITICAL_ERROR) {
         isRetryBarVisibleSingleLiveEvent.setValue(true);
      }

      if (isMapReady) {
         mapViewStateMediatorLiveData.setValue(
             new MapViewState(
                 restaurantsMarkers,
                 isProgressBarVisible
             )
         );
      }
   }

   private void map(@NonNull RestaurantResponseWrapper restaurantResponseWrapper, List<RestaurantMarker> restaurantsMarkers) {
      if (restaurantResponseWrapper.getNearbySearchResponse() != null) {
         for (RestaurantResponse response : restaurantResponseWrapper.getNearbySearchResponse().getResults()) {
            if (response.getBusinessStatus() != null && response.getBusinessStatus().equals("OPERATIONAL")) {
               restaurantsMarkers.add(
                   new RestaurantMarker(
                       response.getPlaceId(),
                       new LatLng(
                           response.getGeometry().getLocation().getLat(),
                           response.getGeometry().getLocation().getLng()
                       ),
                       response.getName(),
                       R.drawable.ic_restaurant_red_marker
                   )
               );
            }
         }
      }
   }

   public SingleLiveEvent<LatLng> getCameraSingleLiveEvent() {
      return cameraSingleLiveEvent;
   }

   public SingleLiveEvent<Boolean> getIsRetryBarVisibleSingleLiveEvent() {
      return isRetryBarVisibleSingleLiveEvent;
   }

   public LiveData<MapViewState> getMapViewStateLiveData() {
      return mapViewStateMediatorLiveData;
   }

   @NonNull
   private String getLocation(@NonNull Location location) {
      return location.getLatitude() + "," + location.getLongitude();
   }

   public void onMapReady() {
      isMapReadyMutableLiveData.setValue(true);
   }

   public void onRetryButtonClicked() {
      nearbySearchRequestPingMutableLiveData.setValue(true);
   }

   public LiveData<Boolean> getNearbySearchRequestPingMutableLiveData() {
      return nearbySearchRequestPingMutableLiveData;
   }

   public void onCameraMovedByUser() {
      gpsModeRepository.setModeUserEnabled(true);
   }

   public void onCameraMoved(@NonNull LatLng cameraPosition) {
      if (Boolean.TRUE.equals(gpsModeRepository.isUserModeEnabledLiveData().getValue())) {
         // If no known last location or if distance between new camera position and last location greater than given value
         if (lastLocation == null || locationDistanceUtil.getDistance(cameraPosition, lastLocation) > MAP_MINIMUM_DISPLACEMENT) {
            // Updates current map location
            Location mapLocation = new Location(LocationManager.GPS_PROVIDER);
            mapLocation.setLatitude(cameraPosition.latitude);
            mapLocation.setLongitude(cameraPosition.longitude);
            mapLocationRepository.setCurrentMapLocation(mapLocation);

            // Updates last location to current camera position
            lastLocation = cameraPosition;
         }
      }

   }

   public void onMyLocationButtonClicked() {
      gpsModeRepository.setModeUserEnabled(false);
   }
}
