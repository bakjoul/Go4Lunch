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
import com.bakjoul.go4lunch.data.repository.GpsLocationRepository;
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
   private final GpsLocationRepository gpsLocationRepository;

   @NonNull
   private final MapLocationRepository mapLocationRepository;

   private final MutableLiveData<Boolean> isMapReadyMutableLiveData = new MutableLiveData<>(false);

   private final MediatorLiveData<MapViewState> mapViewStateMediatorLiveData = new MediatorLiveData<>();

   private final MutableLiveData<Boolean> nearbySearchRequestPingMutableLiveData = new MutableLiveData<>(true);

   private final MutableLiveData<Boolean> isProgressBarVisibleLiveData = new MutableLiveData<>(true);

   private final MutableLiveData<ErrorType> errorTypeMutableLiveData = new MutableLiveData<>();

   @Inject
   public MapViewModel(
       @NonNull GpsLocationRepository gpsLocationRepository,
       @NonNull RestaurantRepository restaurantRepository,
       @NonNull LocationDistanceUtil locationDistanceUtil,
       @NonNull MapLocationRepository mapLocationRepository
   ) {
      this.gpsLocationRepository = gpsLocationRepository;
      this.locationDistanceUtil = locationDistanceUtil;
      this.mapLocationRepository = mapLocationRepository;

      LiveData<Location> gpsLocationLiveData = gpsLocationRepository.getCurrentLocation();
      LiveData<Location> mapLocationLiveData = mapLocationRepository.getCurrentMapLocation();
      LiveData<Boolean> isLocationGpsBasedMutableLiveData = gpsLocationRepository.getIsLocationGpsBasedMutableLiveData();

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

      mapViewStateMediatorLiveData.addSource(isMapReadyMutableLiveData, isMapReady ->
          combine(isMapReady, isLocationGpsBasedMutableLiveData.getValue(), gpsLocationLiveData.getValue(), mapLocationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(isLocationGpsBasedMutableLiveData, isLocationGpsBased ->
          combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBased, gpsLocationLiveData.getValue(), mapLocationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(gpsLocationLiveData, gpsLocation ->
          combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue(), gpsLocation, mapLocationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(mapLocationLiveData, mapLocation ->
          combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue(), gpsLocationLiveData.getValue(), mapLocation, restaurantsMarkersLiveData.getValue(), isProgressBarVisibleLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(restaurantsMarkersLiveData, restaurantMarkers ->
          combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue(), gpsLocationLiveData.getValue(), mapLocationLiveData.getValue(), restaurantMarkers, isProgressBarVisibleLiveData.getValue())
      );
      mapViewStateMediatorLiveData.addSource(isProgressBarVisibleLiveData, isProgressBarVisible ->
          combine(isMapReadyMutableLiveData.getValue(), isLocationGpsBasedMutableLiveData.getValue(), gpsLocationLiveData.getValue(), mapLocationLiveData.getValue(), restaurantsMarkersLiveData.getValue(), isProgressBarVisible)
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
       @Nullable Boolean isMapReady,
       @Nullable Boolean isLocationGpsBased,
       @Nullable Location gpsLocation,
       @Nullable Location mapLocation,
       @Nullable List<RestaurantMarker> restaurantMarkers,
       @Nullable Boolean isProgressBarVisible
   ) {
      if (isMapReady == null || isLocationGpsBased == null || isProgressBarVisible == null) {
         return;
      }

      if(isLocationGpsBased) {

      }

      if (isMapReady) {
         mapViewStateMediatorLiveData.setValue(
             new MapViewState(
                 isLocationGpsBased ? new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude()) : new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude()),
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
         gpsLocationRepository.onCameraMoved();

         Location mapLocation = new Location(LocationManager.GPS_PROVIDER);
         mapLocation.setLatitude(newPosition.latitude);
         mapLocation.setLongitude(newPosition.longitude);
         mapLocationRepository.setCurrentMapLocation(mapLocation);
         Log.d("test", "onCameraMoved: " + locationDistanceUtil.getDistance(newPosition, oldPosition));
      }
   }

   public void onMyLocationButtonClicked() {
      gpsLocationRepository.onMyLocationButtonClicked();
   }
}
