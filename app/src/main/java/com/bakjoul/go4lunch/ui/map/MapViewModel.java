package com.bakjoul.go4lunch.ui.map;

import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.RANK_BY;
import static com.bakjoul.go4lunch.data.repository.RestaurantRepository.TYPE;

import android.location.Location;
import android.location.LocationManager;
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
import com.bakjoul.go4lunch.data.model.ErrorType;
import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.MapLocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapViewModel extends ViewModel {

   private static final String TAG = "MapViewModel";
   private static final double MAP_MINIMUM_DISPLACEMENT = 1000;

   @NonNull
   private final LocationDistanceUtil locationDistanceUtil;

   @NonNull
   private final MapLocationRepository mapLocationRepository;

   private final MutableLiveData<Boolean> isMapReadyMutableLiveData = new MutableLiveData<>(false);

   private final MutableLiveData<Boolean> isLocationGpsBasedMutableLiveData = new MutableLiveData<>(true);

   private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

   private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

   private final MutableLiveData<Boolean> isProgressBarVisibleLiveData = new MutableLiveData<>(true);

   private final MutableLiveData<ErrorType> errorTypeMutableLiveData = new MutableLiveData<>();

   @Inject
   public MapViewModel(
       @NonNull LocationRepository locationRepository,
       @NonNull RestaurantRepository restaurantRepository,
       @NonNull LocationDistanceUtil locationDistanceUtil,
       @NonNull MapLocationRepository mapLocationRepository
   ) {
      this.locationDistanceUtil = locationDistanceUtil;
      this.mapLocationRepository = mapLocationRepository;

      LiveData<Location> gpsLocationLiveData = locationRepository.getCurrentLocation();
      LiveData<Location> mapLocationLiveData = mapLocationRepository.getCurrentMapLocation();

      LiveData<List<RestaurantMarker>> restaurantsMarkersLiveData = Transformations.switchMap(
          isLocationGpsBasedMutableLiveData,
          isLocationGpsBased -> {
             if (isLocationGpsBased) {
                return Transformations.switchMap(
                    gpsLocationLiveData,
                    gpsLocation -> {
                       if (gpsLocation == null) {
                          return new MutableLiveData<>(null);
                       }
                       return getRestaurantsMarkersLiveData(gpsLocation, restaurantRepository);
                    }
                );
             } else {
                return Transformations.switchMap(
                    mapLocationLiveData,
                    mapLocation -> {
                       if (mapLocation == null) {
                          return new MutableLiveData<>(null);
                       }
                       return getRestaurantsMarkersLiveData(mapLocation, restaurantRepository);
                    }
                );
             }
          }
      );

      mapViewStateMediatorLiveData.addSource(gpsLocationLiveData, location ->
          combine(location, restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue(), isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(restaurantsMarkersLiveData, markerOptions ->
          combine(gpsLocationLiveData.getValue(), markerOptions, isProgressBarVisibleLiveData.getValue(), isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(isProgressBarVisibleLiveData, isProgressBarVisible ->
          combine(gpsLocationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisible, isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(isMapReadyMutableLiveData, isMapReady ->
          combine(gpsLocationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue(), isMapReady, isLocationGpsBasedMutableLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(isLocationGpsBasedMutableLiveData, isLocationGpsBased ->
          combine(gpsLocationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue(), isMapReadyMutableLiveData.getValue(), isLocationGpsBased)
      );
   }

   @NonNull
   private LiveData<List<RestaurantMarker>> getRestaurantsMarkersLiveData(Location location, @NonNull RestaurantRepository restaurantRepository) {
      return Transformations.switchMap(
          nearbySearchRequestPingMutableLiveData,
          aVoid -> {
             errorTypeMutableLiveData.setValue(null);
             isProgressBarVisibleLiveData.setValue(true);
             return Transformations.map(
                 restaurantRepository.getNearbySearchResult(getLocation(location), RANK_BY, TYPE, BuildConfig.MAPS_API_KEY),
                 result -> {
                    List<RestaurantMarker> restaurantsMarkers = new ArrayList<>();
                    if (result.getResponse() != null) {
                       isProgressBarVisibleLiveData.setValue(false);
                       for (RestaurantResponse r : result.getResponse().getResults()) {
                          if (r.getBusinessStatus() != null && r.getBusinessStatus().equals("OPERATIONAL")) {
                             restaurantsMarkers.add(
                                 new RestaurantMarker(
                                     r.getPlaceId(),
                                     new LatLng(
                                         r.getGeometry().getLocation().getLat(),
                                         r.getGeometry().getLocation().getLng()
                                     ),
                                     r.getName(),
                                     R.drawable.ic_restaurant_red_marker
                                 )
                             );
                          }
                       }
                    } else if (result.getErrorType() == ErrorType.TIMEOUT) {
                       isProgressBarVisibleLiveData.setValue(false);
                       errorTypeMutableLiveData.setValue(ErrorType.TIMEOUT);
                       Log.d(TAG, "Socket time out");
                    } else {
                       isProgressBarVisibleLiveData.setValue(false);
                       return restaurantsMarkers;
                    }
                    return restaurantsMarkers;
                 }
             );
          }
      );
   }


   private void combine(
       @Nullable Location location,
       @Nullable List<RestaurantMarker> restaurantMarkers,
       @Nullable Boolean isProgressBarVisible,
       @Nullable Boolean isMapReady,
       @Nullable Boolean isLocationGpsBased) {
      if (location == null || isProgressBarVisible == null || isMapReady == null || isLocationGpsBased == null) {
         return;
      }

      if (isMapReady) {
         mapViewStateMediatorLiveData.setValue(
             new MapViewState(
                 new LatLng(location.getLatitude(), location.getLongitude()),
                 restaurantMarkers == null ? new ArrayList<>() : restaurantMarkers,
                 errorTypeMutableLiveData.getValue() == null ? null : errorTypeMutableLiveData.getValue(),
                 isProgressBarVisible,
                 isLocationGpsBased)
         );
      }
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

   public void onCameraMoved(@NonNull LatLng newPosition, @NonNull LatLng oldPosition) {
      if (locationDistanceUtil.getDistance(newPosition, oldPosition) > MAP_MINIMUM_DISPLACEMENT) {
         isLocationGpsBasedMutableLiveData.setValue(false);

         Location mapLocation = new Location(LocationManager.GPS_PROVIDER);
         mapLocation.setLatitude(newPosition.latitude);
         mapLocation.setLongitude(newPosition.longitude);
         mapLocationRepository.setCurrentMapLocation(mapLocation);
         Log.d("test", "onCameraMoved: " + locationDistanceUtil.getDistance(newPosition, oldPosition));
      }
   }

   public void onMyLocationButtonClicked() {
      isLocationGpsBasedMutableLiveData.setValue(true);
   }
}
