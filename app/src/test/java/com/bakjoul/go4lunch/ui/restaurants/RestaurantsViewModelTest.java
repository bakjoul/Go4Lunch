package com.bakjoul.go4lunch.ui.restaurants;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.LocationDistanceUtils;
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
        new OpeningHoursResponse(true),
        new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
        5.0,
        new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
        "OPERATIONAL",
        75);
    private static final RestaurantResponse RESTAURANT_RESPONSE_2 = new RestaurantResponse(
        "2",
        "RESTAURANT_2_NAME",
        "RESTAURANT_2_ADDRESS",
        new OpeningHoursResponse(false),
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
    private final RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class);
    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final LocationDistanceUtils locationDistanceUtils = Mockito.mock(LocationDistanceUtils.class);
    private final RestaurantImageMapper restaurantImageMapper = Mockito.mock(RestaurantImageMapper.class);

    private final Location location = Mockito.mock(Location.class);

    private final MutableLiveData<NearbySearchResponse> nearbySearchResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    private RestaurantsViewModel viewModel;

    @Before
    public void setUp() {
        given(application.getString(R.string.restaurant_item_is_open)).willReturn(OPEN);
        given(application.getString(R.string.restaurant_item_is_closed)).willReturn(CLOSED);
        given(application.getString(R.string.restaurant_item_info_not_available)).willReturn(NOT_AVAILABLE);

        doReturn(nearbySearchResponseLiveData).when(restaurantRepository).getNearbySearchResponse(eq(getLatLngToString(FAKE_LOCATION)), eq("distance"), eq("restaurant"), anyString());
        doReturn(locationLiveData).when(locationRepository).getCurrentLocation();
        doReturn("50m").when(locationDistanceUtils).getDistance(location, new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude));
        doReturn("ImageUrl").when(restaurantImageMapper).getImageUrl("fakePhotoReference");

        doReturn(FAKE_LOCATION.latitude).when(location).getLatitude();
        doReturn(FAKE_LOCATION.longitude).when(location).getLongitude();

        locationLiveData.setValue(location);

        viewModel = new RestaurantsViewModel(application, restaurantRepository, locationRepository, locationDistanceUtils, restaurantImageMapper);

        verify(locationRepository).getCurrentLocation();
    }

    @Test
    public void nominal_case() {
        // Given
        nearbySearchResponseLiveData.setValue(getDefaultNearbySearchResponse());

        // When
        RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewState());

        // Then
        assertEquals(getDefaultRestaurantViewState(), result);
    }

    @Test
    public void nearbysearchresponse_is_null_should_expose_empty_viewstate() {
        // Given
        nearbySearchResponseLiveData.setValue(null);

        // When
        RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewState());

        // Then
        assertEquals(getEmptyRestaurantViewState(), result);
    }

    @Test
    public void location_is_null_should_expose_empty_viewState() {
        // Given
        locationLiveData.setValue(null);

        // When
        RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewState());

        // Then
        assertEquals(getEmptyRestaurantViewState(), result);
    }


    // region IN
    @NonNull
    private NearbySearchResponse getDefaultNearbySearchResponse() {
        return new NearbySearchResponse(
            new ArrayList<>(Arrays.asList(RESTAURANT_RESPONSE_1, RESTAURANT_RESPONSE_2, RESTAURANT_RESPONSE_3, RESTAURANT_RESPONSE_4)),
            "OK"
        );
    }

    @NonNull
    private String getLatLngToString(@NonNull LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
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
                "ImageUrl"
            )
        );
        restaurantsItemViewStateList.add(
            new RestaurantsItemViewState(
                RESTAURANT_RESPONSE_2.getPlaceId(),
                RESTAURANT_RESPONSE_2.getName(),
                RESTAURANT_RESPONSE_2.getVicinity(),
                CLOSED,
                locationDistanceUtils.getDistance(location, RESTAURANT_RESPONSE_2.getGeometry().getLocation()),
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
                locationDistanceUtils.getDistance(location, RESTAURANT_RESPONSE_3.getGeometry().getLocation()),
                "",
                0,
                false,
                null
            )
        );
        return new RestaurantsViewState(restaurantsItemViewStateList, false);
    }

    @NonNull
    private RestaurantsViewState getEmptyRestaurantViewState() {
        return new RestaurantsViewState(new ArrayList<>(), true);
    }
    // endregion OUT
}