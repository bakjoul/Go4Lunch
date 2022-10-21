package com.bakjoul.go4lunch.ui.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.GeometryResponse;
import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.restaurant.RestaurantMarker;
import com.bakjoul.go4lunch.data.restaurant.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.GpsLocationRepository;
import com.bakjoul.go4lunch.data.repository.GpsModeRepository;
import com.bakjoul.go4lunch.data.repository.MapLocationRepository;
import com.bakjoul.go4lunch.data.restaurant.RestaurantRepository;
import com.bakjoul.go4lunch.data.restaurant.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapViewModelTest {

   // region Constants
   private static final LatLng DEFAULT_LOCATION = new LatLng(48.841577, 2.253059);
   private static final LatLng FAKE_LOCATION = new LatLng(43.21, 12.34);

   private static final RestaurantResponse RESTAURANT_RESPONSE_1 = new RestaurantResponse(
       "1",
       "RESTAURANT_1_NAME",
       "RESTAURANT_1_ADDRESS",
       new OpeningHoursResponse(true, null, null),
       new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
       5.0,
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "OPERATIONAL",
       75);
   private static final RestaurantResponse RESTAURANT_RESPONSE_2 = new RestaurantResponse(
       "2",
       "RESTAURANT_2_NAME",
       "RESTAURANT_2_ADDRESS",
       new OpeningHoursResponse(false, null, null),
       new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
       4.4,
       new ArrayList<>(Collections.singletonList(new PhotoResponse(null))),
       "OPERATIONAL",
       75);
   private static final RestaurantResponse RESTAURANT_RESPONSE_3 = new RestaurantResponse(
       "3",
       "RESTAURANT_3_NAME",
       "RESTAURANT_3_ADDRESS",
       null,
       new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
       0.0,
       null,
       "OPERATIONAL",
       0);
   private static final RestaurantResponse RESTAURANT_RESPONSE_4 = new RestaurantResponse(
       "4",
       "RESTAURANT_4_NAME",
       "RESTAURANT_4_ADDRESS",
       null,
       new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
       1.2,
       null,
       "CLOSED_TEMPORARILY",
       75);
   private static final RestaurantResponse RESTAURANT_RESPONSE_5 = new RestaurantResponse(
       "5",
       "RESTAURANT_5_NAME",
       "RESTAURANT_5_ADDRESS",
       null,
       new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
       1.2,
       null,
       null,
       75);
   // endregion Constants

   @Rule
   public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

   private final GpsLocationRepository gpsLocationRepository = Mockito.mock(GpsLocationRepository.class);
   private final MapLocationRepository mapLocationRepository = Mockito.mock(MapLocationRepository.class);
   private final GpsModeRepository gpsModeRepository = Mockito.mock(GpsModeRepository.class);
   private final RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class);
   private final LocationDistanceUtil locationDistanceUtil = Mockito.mock(LocationDistanceUtil.class);

   private final MutableLiveData<Boolean> isUserModeEnabledLiveData = new MutableLiveData<>();
   private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
   private final MutableLiveData<RestaurantResponseWrapper> responseWrapperMutableLiveData = new MutableLiveData<>();

   private final Location location = Mockito.mock(Location.class);

   private MapViewModel viewModel;

   @Before
   public void setUp() {
      doReturn(FAKE_LOCATION.latitude).when(location).getLatitude();
      doReturn(FAKE_LOCATION.longitude).when(location).getLongitude();
      locationLiveData.setValue(location);

      isUserModeEnabledLiveData.setValue(false);
      doReturn(isUserModeEnabledLiveData).when(gpsModeRepository).isUserModeEnabledLiveData();

      doReturn(locationLiveData).when(gpsLocationRepository).getCurrentLocationLiveData();
      doReturn(locationLiveData).when(mapLocationRepository).getCurrentMapLocationLiveData();

      doReturn(responseWrapperMutableLiveData).when(restaurantRepository).getNearbySearchResponse(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      viewModel = new MapViewModel(gpsLocationRepository, mapLocationRepository, gpsModeRepository, restaurantRepository, locationDistanceUtil);
   }

   @Test
   public void gps_location_nominal_case() {
      // Given
      viewModel.onMapReady();
      viewModel.onMyLocationButtonClicked();
      viewModel.onCameraMoved(FAKE_LOCATION);
      responseWrapperMutableLiveData.setValue(getDefaultRestaurantResponseWrapper());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertEquals(getDefaultMapViewState(), result);
   }

   @Test
   public void map_not_ready_should_expose_null_viewstate() {
      // Given
      responseWrapperMutableLiveData.setValue(getDefaultRestaurantResponseWrapper());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertNull(result);
   }

   @Test
   public void usermode_location_nominal_case() {
      // Given
      isUserModeEnabledLiveData.setValue(true);
      viewModel.onMapReady();
      viewModel.onCameraMovedByUser();
      viewModel.onCameraMoved(FAKE_LOCATION);
      responseWrapperMutableLiveData.setValue(getDefaultRestaurantResponseWrapper());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());
      Boolean isModeUserEnabled = LiveDataTestUtil.getValueForTesting(gpsModeRepository.isUserModeEnabledLiveData());

      // Then
      assertEquals(getDefaultMapViewState(), result);
      assertEquals(true, isModeUserEnabled);
   }

   @Test
   public void location_not_null_should_set_camera_singleLiveEvent_to_location_value() {
      // When
      LatLng result = LiveDataTestUtil.getValueForTesting(viewModel.getCameraSingleLiveEvent());

      // Then
      assertEquals(getDefaultCameraLatLng(), result);
   }

   @Test
   public void location_null_then_camera_singleLiveEvent_should_be_null() {
      // Given
      locationLiveData.setValue(null);

      // When
      LatLng result = LiveDataTestUtil.getValueForTesting(viewModel.getCameraSingleLiveEvent());

      // Then
      assertNull(result);
   }

   @Test
   public void location_null_should_expose_viewstate_with_empty_markers() {
      // Given
      viewModel.onMapReady();
      locationLiveData.setValue(null);

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertEquals(getViewStateWithEmptyMarkers(), result);
   }

   @Test
   public void null_nearBySearchResponse_with_ioError_should_expose_viewstate_with_empty_markers_and_retry_bar() {
      // Given
      viewModel.onMapReady();
      doReturn(getNullResponseWithIoError()).when(restaurantRepository).getNearbySearchResponse(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());
      Boolean isRetryBarVisible = LiveDataTestUtil.getValueForTesting(viewModel.getIsRetryBarVisibleSingleLiveEvent());

      // Then
      assertEquals(getViewStateWithEmptyMarkersAndRetryBar(), result);
      assertTrue(isRetryBarVisible);
   }

   @Test
   public void null_nearBySearchResponse_with_criticalError_should_expose_viewstate_with_empty_markers_and_retry_bar() {
      // Given
      viewModel.onMapReady();
      doReturn(getNullResponseWithCriticalError()).when(restaurantRepository).getNearbySearchResponse(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());
      Boolean isRetryBarVisible = LiveDataTestUtil.getValueForTesting(viewModel.getIsRetryBarVisibleSingleLiveEvent());

      // Then
      assertEquals(getViewStateWithEmptyMarkersAndRetryBar(), result);
      assertTrue(isRetryBarVisible);
   }

   @Test
   public void nearBySearchResponse_with_criticalError_should_expose_viewstate_with_empty_markers_and_retry_bar() {
      // Given
      viewModel.onMapReady();
      doReturn(getResponseWithCriticalError()).when(restaurantRepository).getNearbySearchResponse(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());
      Boolean isRetryBarVisible = LiveDataTestUtil.getValueForTesting(viewModel.getIsRetryBarVisibleSingleLiveEvent());

      // Then
      assertEquals(getViewStateWithEmptyMarkersAndRetryBar(), result);
      assertTrue(isRetryBarVisible);
   }

   @Test
   public void map_is_not_ready_should_expose_null_viewstate() {
      // Given that we do not call onMapReady()

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertNull(result);
   }

   @Test
   public void onRetryButtonClicked_should_update_PingLiveData() {
      // When
      viewModel.onRetryButtonClicked();

      // Then
      assertEquals(Boolean.TRUE, viewModel.getNearbySearchRequestPingMutableLiveData().getValue());
   }

   // region IN
   @NonNull
   private String getLatLngToString(@NonNull LatLng latLng) {
      return latLng.latitude + "," + latLng.longitude;
   }

   @NonNull
   private RestaurantResponseWrapper getDefaultRestaurantResponseWrapper() {
      return new RestaurantResponseWrapper(
          new NearbySearchResponse(new ArrayList<>(Arrays.asList(RESTAURANT_RESPONSE_1, RESTAURANT_RESPONSE_2, RESTAURANT_RESPONSE_3, RESTAURANT_RESPONSE_4, RESTAURANT_RESPONSE_5)), "OK"),
          RestaurantResponseWrapper.State.SUCCESS
      );
   }

   @NonNull
   private MutableLiveData<RestaurantResponseWrapper> getNullResponseWithIoError() {
      return new MutableLiveData<>(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.IO_ERROR));
   }

   @NonNull
   private MutableLiveData<RestaurantResponseWrapper> getNullResponseWithCriticalError() {
      return new MutableLiveData<>(new RestaurantResponseWrapper(new NearbySearchResponse(new ArrayList<>(Arrays.asList(RESTAURANT_RESPONSE_1, RESTAURANT_RESPONSE_2, RESTAURANT_RESPONSE_3, RESTAURANT_RESPONSE_4)), "OK"), RestaurantResponseWrapper.State.CRITICAL_ERROR));
   }

   @NonNull
   private MutableLiveData<RestaurantResponseWrapper> getResponseWithCriticalError() {
      return new MutableLiveData<>(new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.CRITICAL_ERROR));
   }
   // endregion IN

   // region OUT
   @NonNull
   private MapViewState getDefaultMapViewState() {
      List<RestaurantMarker> restaurantMarkers = new ArrayList<>();
      restaurantMarkers.add(
          new RestaurantMarker(
              RESTAURANT_RESPONSE_1.getPlaceId(),
              new LatLng(
                  RESTAURANT_RESPONSE_1.getGeometry().getLocation().getLat(),
                  RESTAURANT_RESPONSE_1.getGeometry().getLocation().getLng()
              ),
              RESTAURANT_RESPONSE_1.getName(),
              R.drawable.ic_restaurant_red_marker
          )
      );
      restaurantMarkers.add(
          new RestaurantMarker(
              RESTAURANT_RESPONSE_2.getPlaceId(),
              new LatLng(
                  RESTAURANT_RESPONSE_2.getGeometry().getLocation().getLat(),
                  RESTAURANT_RESPONSE_2.getGeometry().getLocation().getLng()
              ),
              RESTAURANT_RESPONSE_2.getName(),
              R.drawable.ic_restaurant_red_marker
          )
      );
      restaurantMarkers.add(
          new RestaurantMarker(
              RESTAURANT_RESPONSE_3.getPlaceId(),
              new LatLng(
                  RESTAURANT_RESPONSE_3.getGeometry().getLocation().getLat(),
                  RESTAURANT_RESPONSE_3.getGeometry().getLocation().getLng()
              ),
              RESTAURANT_RESPONSE_3.getName(),
              R.drawable.ic_restaurant_red_marker
          )
      );

      return new MapViewState(
          restaurantMarkers,
          false
      );
   }

   @NonNull
   private LatLng getDefaultCameraLatLng() {
      return new LatLng(location.getLatitude(), location.getLongitude());
   }

   @NonNull
   private MapViewState getViewStateWithEmptyMarkers() {
      return new MapViewState(
          new ArrayList<>(),
          false
      );
   }

   @NonNull
   private MapViewState getViewStateWithEmptyMarkersAndRetryBar() {
      return new MapViewState(
          new ArrayList<>(),
          false
      );
   }
   // endregion OUT
}