package com.bakjoul.go4lunch.ui.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.ErrorType;
import com.bakjoul.go4lunch.data.model.GeometryResponse;
import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResult;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.GpsLocationRepository;
import com.bakjoul.go4lunch.data.repository.MapLocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
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
   // endregion Constants

   @Rule
   public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

   private final GpsLocationRepository gpsLocationRepository = Mockito.mock(GpsLocationRepository.class);
   private final RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class);
   private final LocationDistanceUtil locationDistanceUtil = Mockito.mock(LocationDistanceUtil.class);
   private final MapLocationRepository mapLocationRepository = Mockito.mock(MapLocationRepository.class);

   private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
   private final MutableLiveData<NearbySearchResult> nearbySearchResultMutableLiveData = new MutableLiveData<>();

   private final Location location = Mockito.mock(Location.class);

   private MapViewModel viewModel;

   @Before
   public void setUp() {
      doReturn(locationLiveData).when(gpsLocationRepository).getCurrentLocation();
      doReturn(nearbySearchResultMutableLiveData).when(restaurantRepository).getNearbySearchResult(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      doReturn(FAKE_LOCATION.latitude).when(location).getLatitude();
      doReturn(FAKE_LOCATION.longitude).when(location).getLongitude();

      locationLiveData.setValue(location);

      viewModel = new MapViewModel(gpsLocationRepository, restaurantRepository, locationDistanceUtil, mapLocationRepository);

      verify(gpsLocationRepository).getCurrentLocation();
   }

   @Test
   public void nominal_case() {
      // Given
      viewModel.onMapReady();
      nearbySearchResultMutableLiveData.setValue(getDefaultNearbySearchResult());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertEquals(getDefaultMapViewState(), result);
   }

   @Test
   public void location_null_should_expose_markersLiveData_null() {
      // Given
      locationLiveData.setValue(null);

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertNull(result);
   }

   @Test
   public void timeout_error_should_expose_empty_markers_viewstate_with_timeout_error() {
      // Given
      viewModel.onMapReady();
      doReturn(new MutableLiveData<>(new NearbySearchResult(null, ErrorType.TIMEOUT))).when(restaurantRepository).getNearbySearchResult(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertEquals(getEmptyMarkersViewStateWithTimeoutError(), result);
   }

   @Test
   public void result_response_null_should_expose_empty_markers_viewstate() {
      // Given
      viewModel.onMapReady();
      doReturn(new MutableLiveData<>(new NearbySearchResult(null, null))).when(restaurantRepository).getNearbySearchResult(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      // When
      MapViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMapViewStateLiveData());

      // Then
      assertEquals(getEmptyMarkersViewState(), result);
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
   private NearbySearchResult getDefaultNearbySearchResult() {
      return new NearbySearchResult(
          new NearbySearchResponse(new ArrayList<>(Arrays.asList(RESTAURANT_RESPONSE_1, RESTAURANT_RESPONSE_2, RESTAURANT_RESPONSE_3, RESTAURANT_RESPONSE_4)), "OK"),
          null
      );
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
              R.drawable.ic_restaurant_green_marker
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
              R.drawable.ic_restaurant_green_marker
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
              R.drawable.ic_restaurant_green_marker
          )
      );

      return new MapViewState(
          FAKE_LOCATION,
          restaurantMarkers,
          null,
          false,
          true);
   }

   @NonNull
   private MapViewState getEmptyMarkersViewStateWithTimeoutError() {
      return new MapViewState(
          new LatLng(location.getLatitude(), location.getLongitude()),
          new ArrayList<>(),
          ErrorType.TIMEOUT,
          false,
          true);
   }

   @NonNull
   private MapViewState getEmptyMarkersViewState() {
      return new MapViewState(
          new LatLng(location.getLatitude(), location.getLongitude()),
          new ArrayList<>(),
          null,
          false,
          true);
   }
   // endregion OUT
}