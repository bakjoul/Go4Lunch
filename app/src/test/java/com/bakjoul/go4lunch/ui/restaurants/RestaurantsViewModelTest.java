
package com.bakjoul.go4lunch.ui.restaurants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import android.app.Application;
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
import com.bakjoul.go4lunch.data.location.GpsLocationRepository;
import com.bakjoul.go4lunch.data.location.LocationModeRepository;
import com.bakjoul.go4lunch.data.location.MapLocationRepository;
import com.bakjoul.go4lunch.data.restaurants.RestaurantRepository;
import com.bakjoul.go4lunch.data.restaurants.RestaurantResponse;
import com.bakjoul.go4lunch.data.restaurants.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtil;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;
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

public class RestaurantsViewModelTest {

   // region Constants
   private static final LatLng DEFAULT_LOCATION = new LatLng(48.841577, 2.253059);
   private static final LatLng FAKE_LOCATION = new LatLng(43.21, 12.34);

   private static final String OPEN = "Ouvert";
   private static final String CLOSED = "Fermé";
   private static final String NOT_AVAILABLE = "Information non disponible";

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

   private final Application application = Mockito.mock(Application.class);
   private final GpsLocationRepository gpsLocationRepository = Mockito.mock(GpsLocationRepository.class);
   private final MapLocationRepository mapLocationRepository = Mockito.mock(MapLocationRepository.class);
   private final LocationModeRepository locationModeRepository = Mockito.mock(LocationModeRepository.class);
   private final RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class);
   private final LocationDistanceUtil locationDistanceUtils = Mockito.mock(LocationDistanceUtil.class);
   private final RestaurantImageMapper restaurantImageMapper = Mockito.mock(RestaurantImageMapper.class);

   private final Location location = Mockito.mock(Location.class);
   private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
   private final MutableLiveData<Boolean> isUserModeEnabledLiveData = new MutableLiveData<>();
   private final MutableLiveData<RestaurantResponseWrapper> responseWrapperMutableLiveData = new MutableLiveData<>();

   private RestaurantsViewModel viewModel;

   @Before
   public void setUp() {
      doReturn(OPEN).when(application).getString(R.string.restaurant_is_open);
      doReturn(CLOSED).when(application).getString(R.string.restaurant_is_closed);
      doReturn(NOT_AVAILABLE).when(application).getString(R.string.information_not_available);

      doReturn(FAKE_LOCATION.latitude).when(location).getLatitude();
      doReturn(FAKE_LOCATION.longitude).when(location).getLongitude();
      locationLiveData.setValue(location);

      isUserModeEnabledLiveData.setValue(false);
      doReturn(isUserModeEnabledLiveData).when(locationModeRepository).isUserModeEnabledLiveData();

      doReturn(locationLiveData).when(gpsLocationRepository).getCurrentLocationLiveData();
      doReturn(locationLiveData).when(mapLocationRepository).getCurrentMapLocationLiveData();

      doReturn(responseWrapperMutableLiveData).when(restaurantRepository).getNearbySearchResponse(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());

      doReturn("50m").when(locationDistanceUtils).getDistanceToStringFormat(location, new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude));
      doReturn("fakeImageUrl").when(restaurantImageMapper).getImageUrl("fakePhotoReference", false);

      viewModel = new RestaurantsViewModel(application, gpsLocationRepository, mapLocationRepository, locationModeRepository, restaurantRepository, locationDistanceUtils, restaurantImageMapper);
   }

   @Test
   public void gps_nominal_case() {
      // Given
      responseWrapperMutableLiveData.setValue(getDefaultRestaurantResponseWrapper());

      // When
      RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewStateLiveData());

      // Then
      assertEquals(getDefaultRestaurantViewState(), result);
   }

   @Test
   public void usermode_nominal_case() {
      // Given
      isUserModeEnabledLiveData.setValue(true);
      responseWrapperMutableLiveData.setValue(getDefaultRestaurantResponseWrapper());

      // When
      RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewStateLiveData());

      // Then
      assertEquals(getDefaultRestaurantViewState(), result);
   }

   @Test
   public void location_null_should_expose_viewstate_with_empty_list() {
      // Given
      locationLiveData.setValue(null);

      // When
      RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewStateLiveData());

      // Then
      assertEquals(getViewStateWithEmptyList(), result);
   }

   @Test
   public void responseWrapper_null_should_expose_viewstate_null() {
      // Given
      responseWrapperMutableLiveData.setValue(null);

      // When
      RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewStateLiveData());

      // Then
      assertNull(result);
   }

   @Test
   public void nearbySearchResponse_null_with_ioError_should_expose_viewstate_with_empty_list_and_retry_bar_visible() {
      // Given
      responseWrapperMutableLiveData.setValue(getNullResponseWithIoError());

      // When
      RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewStateLiveData());
      Boolean isRetryBarVisible = LiveDataTestUtil.getValueForTesting(viewModel.getIsRetryBarVisibleSingleLiveEvent());

      // Then
      assertEquals(getViewStateWithEmptyList(), result);
      assertTrue(isRetryBarVisible);
   }

   @Test
   public void nearBySearchResponse_null_with_criticalError_should_expose_viewstate_with_empty_list_and_retry_bar_visible() {
      // Given
      responseWrapperMutableLiveData.setValue(getNullResponseWithCriticalError());

      // When
      RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewStateLiveData());
      Boolean isRetryBarVisible = LiveDataTestUtil.getValueForTesting(viewModel.getIsRetryBarVisibleSingleLiveEvent());

      // Then
      assertEquals(getViewStateWithEmptyList(), result);
      assertTrue(isRetryBarVisible);
   }

   @Test
   public void nearBySearchResponse_with_criticalError_should_expose_viewstate_with_empty_markers_and_retry_bar_visible() {
      // Given
      responseWrapperMutableLiveData.setValue(getResponseWithCriticalError());

      // When
      RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewStateLiveData());
      Boolean isRetryBarVisible = LiveDataTestUtil.getValueForTesting(viewModel.getIsRetryBarVisibleSingleLiveEvent());

      // Then
      assertEquals(getViewStateWithEmptyList(), result);
      assertTrue(isRetryBarVisible);
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
          new NearbySearchResponse(new ArrayList<>(Arrays.asList(RESTAURANT_RESPONSE_1, RESTAURANT_RESPONSE_2, RESTAURANT_RESPONSE_3, RESTAURANT_RESPONSE_4)), "OK"),
          RestaurantResponseWrapper.State.SUCCESS
      );
   }

   @NonNull
   private RestaurantResponseWrapper getNullResponseWithIoError() {
      return new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.IO_ERROR);
   }

   @NonNull
   private RestaurantResponseWrapper getNullResponseWithCriticalError() {
      return new RestaurantResponseWrapper(null, RestaurantResponseWrapper.State.CRITICAL_ERROR);
   }

   @NonNull
   private RestaurantResponseWrapper getResponseWithCriticalError() {
      return new RestaurantResponseWrapper(new NearbySearchResponse(new ArrayList<>(Arrays.asList(RESTAURANT_RESPONSE_1, RESTAURANT_RESPONSE_2, RESTAURANT_RESPONSE_3, RESTAURANT_RESPONSE_4)), "OK"), RestaurantResponseWrapper.State.CRITICAL_ERROR);
   }
   // endregion IN

   // region OUT
   @NonNull
   private RestaurantsViewState getDefaultRestaurantViewState() {
      List<RestaurantsItemViewState> restaurantsItemViewStateList = new ArrayList<>();
      restaurantsItemViewStateList.add(
          new RestaurantsItemViewState(
              RESTAURANT_RESPONSE_1.getPlaceId(),
              RESTAURANT_RESPONSE_1.getName(),
              RESTAURANT_RESPONSE_1.getVicinity(),
              OPEN,
              "50m",
              "",
              3,
              true,
              "fakeImageUrl"
          )
      );
      restaurantsItemViewStateList.add(
          new RestaurantsItemViewState(
              RESTAURANT_RESPONSE_2.getPlaceId(),
              RESTAURANT_RESPONSE_2.getName(),
              RESTAURANT_RESPONSE_2.getVicinity(),
              CLOSED,
              locationDistanceUtils.getDistanceToStringFormat(location, RESTAURANT_RESPONSE_2.getGeometry().getLocation()),
              "",
              3,
              true,
              null
          )
      );
      restaurantsItemViewStateList.add(
          new RestaurantsItemViewState(
              RESTAURANT_RESPONSE_3.getPlaceId(),
              RESTAURANT_RESPONSE_3.getName(),
              RESTAURANT_RESPONSE_3.getVicinity(),
              NOT_AVAILABLE,
              locationDistanceUtils.getDistanceToStringFormat(location, RESTAURANT_RESPONSE_3.getGeometry().getLocation()),
              "",
              0,
              false,
              null
          )
      );
      return new RestaurantsViewState(restaurantsItemViewStateList, false, false);
   }

   @NonNull
   private RestaurantsViewState getViewStateWithEmptyList() {
      return new RestaurantsViewState(new ArrayList<>(), true, false);
   }
   // endregion OUT
}
