package com.bakjoul.go4lunch.ui.restaurants;


import static org.mockito.ArgumentMatchers.anyString;

import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.model.GeometryResponse;
import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;


public class RestaurantsViewModelTest {

    // region Constants
    private static final LatLng DEFAULT_LOCATION = new LatLng(48.841577, 2.253059);

    private static final RestaurantResponse RESTAURANT_RESPONSE_1 = new RestaurantResponse(
        "1",
        "RESTAURANT_1_NAME",
        "RESTAURANT_1_ADDRESS",
        new OpeningHoursResponse(true),
        new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
        3.0,
        null,
        "OPERATIONAL",
        75);
    private static final RestaurantResponse RESTAURANT_RESPONSE_2 = new RestaurantResponse(
        "2",
        "RESTAURANT_2_NAME",
        "RESTAURANT_2_ADDRESS",
        new OpeningHoursResponse(true),
        new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
        2.5,
        null,
        "OPERATIONAL",
        75);
    private static final RestaurantResponse RESTAURANT_RESPONSE_3 = new RestaurantResponse(
        "3",
        "RESTAURANT_3_NAME",
        "RESTAURANT_3_ADDRESS",
        new OpeningHoursResponse(true),
        new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
        2.0,
        null,
        "OPERATIONAL",
        75);
    // endregion

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class);
    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final RestaurantImageMapper restaurantImageMapper = Mockito.mock(RestaurantImageMapper.class);

    private final MutableLiveData<NearbySearchResponse> nearbySearchResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    private RestaurantsViewModel viewModel;

    @Before
    public void setUp() {
        Mockito.doReturn(nearbySearchResponseLiveData).when(restaurantRepository).getNearbySearchResponse(anyString(), anyString(), anyString(), anyString());
        Mockito.doReturn(locationLiveData).when(locationRepository).getCurrentLocation();

        nearbySearchResponseLiveData.setValue(getDefaultNearbySearchResponse());
        locationLiveData.setValue(getDefaultLocation());

        viewModel = new RestaurantsViewModel(restaurantRepository, locationRepository, restaurantImageMapper);

        Mockito.verify(restaurantRepository).getNearbySearchResponse(anyString(), anyString(), anyString(), anyString());
        Mockito.verify(locationRepository).getCurrentLocation();
    }


    // region IN
    @NonNull
    private NearbySearchResponse getDefaultNearbySearchResponse() {
        return new NearbySearchResponse(
            new ArrayList<>(Arrays.asList(RESTAURANT_RESPONSE_1, RESTAURANT_RESPONSE_2, RESTAURANT_RESPONSE_3)),
            "OK"
        );
    }

    @NonNull
    private Location getDefaultLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);
        return location;
    }

    // endregion
}