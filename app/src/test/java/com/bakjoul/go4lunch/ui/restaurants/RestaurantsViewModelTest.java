package com.bakjoul.go4lunch.ui.restaurants;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.model.GeometryResponse;
import com.bakjoul.go4lunch.data.model.LocationResponse;
import com.bakjoul.go4lunch.data.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.bakjoul.go4lunch.ui.utils.RestaurantDistanceComputer;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RestaurantsViewModelTest {

    // region Constants
    private static final LatLng DEFAULT_LOCATION = new LatLng(48.841577, 2.253059);

    private static final RestaurantResponse RESTAURANT_RESPONSE_1 = new RestaurantResponse(
        "1",
        "RESTAURANT_1_NAME",
        "RESTAURANT_1_ADDRESS",
        new OpeningHoursResponse(true),
        new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
        5.0,
        null,
        "OPERATIONAL",
        75);
    private static final RestaurantResponse RESTAURANT_RESPONSE_2 = new RestaurantResponse(
        "2",
        "RESTAURANT_2_NAME",
        "RESTAURANT_2_ADDRESS",
        new OpeningHoursResponse(true),
        new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
        4.4,
        null,
        "OPERATIONAL",
        75);
    private static final RestaurantResponse RESTAURANT_RESPONSE_3 = new RestaurantResponse(
        "3",
        "RESTAURANT_3_NAME",
        "RESTAURANT_3_ADDRESS",
        new OpeningHoursResponse(true),
        new GeometryResponse(new LocationResponse(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)),
        2.2,
        null,
        "OPERATIONAL",
        75);
    // endregion

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class);
    private final LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
    private final RestaurantDistanceComputer restaurantDistanceComputer = Mockito.mock(RestaurantDistanceComputer.class);
    private final RestaurantImageMapper restaurantImageMapper = Mockito.mock(RestaurantImageMapper.class);

    private final MutableLiveData<NearbySearchResponse> nearbySearchResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private String distance;
    private String url;

    private RestaurantsViewModel viewModel;

    @Before
    public void setUp() {
        Mockito.doReturn(nearbySearchResponseLiveData).when(restaurantRepository).getNearbySearchResponse(anyString(), anyString(), anyString(), anyString());
        Mockito.doReturn(locationLiveData).when(locationRepository).getCurrentLocation();
        Mockito.doReturn(distance).when(restaurantDistanceComputer).getDistance(ArgumentMatchers.any(Location.class), ArgumentMatchers.any(LocationResponse.class));
        Mockito.doReturn(url).when(restaurantImageMapper).getImageUrl(anyString());

        nearbySearchResponseLiveData.setValue(getDefaultNearbySearchResponse());
        locationLiveData.setValue(getDefaultLocation());
        distance = "";
        url = "";

        viewModel = new RestaurantsViewModel(restaurantRepository, locationRepository, restaurantDistanceComputer, restaurantImageMapper);

/*        Mockito.verify(restaurantRepository).getNearbySearchResponse(anyString(), anyString(), anyString(), anyString());
        Mockito.verify(locationRepository).getCurrentLocation();
        Mockito.verify(restaurantDistanceComputer).getDistance(ArgumentMatchers.any(Location.class), ArgumentMatchers.any(LocationResponse.class));
        Mockito.verify(restaurantImageMapper).getImageUrl(anyString());*/
    }

    @Test
    public void nominal_case() {
        // When
        RestaurantsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getRestaurantsViewState());

        // Then
        assertEquals(getDefaultRestaurantViewState(), result);
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
        Location location = new Location("test");
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);
        return location;
    }
    // endregion

    // region OUT
    @NonNull
    private RestaurantsViewState getDefaultRestaurantViewState() {
        List<RestaurantsItemViewState> restaurantsItemViewStateList = new ArrayList<>();
        restaurantsItemViewStateList.add(
            new RestaurantsItemViewState(
                RESTAURANT_RESPONSE_1.getPlaceId(),
                RESTAURANT_RESPONSE_1.getName(),
                RESTAURANT_RESPONSE_1.getVicinity(),
                checkIfOpen(RESTAURANT_RESPONSE_1.getOpeningHours().getOpenNow()),
                restaurantDistanceComputer.getDistance(getDefaultLocation(), RESTAURANT_RESPONSE_1.getGeometry().getLocation()),
                "",
                convertRating(RESTAURANT_RESPONSE_1.getRating()),
                isRatingBarVisble(RESTAURANT_RESPONSE_1.getUserRatingsTotal()),
                restaurantImageMapper.getImageUrl(getPhotoRef(RESTAURANT_RESPONSE_1))
            )
        );
        restaurantsItemViewStateList.add(
            new RestaurantsItemViewState(
                RESTAURANT_RESPONSE_2.getPlaceId(),
                RESTAURANT_RESPONSE_2.getName(),
                RESTAURANT_RESPONSE_2.getVicinity(),
                checkIfOpen(RESTAURANT_RESPONSE_2.getOpeningHours().getOpenNow()),
                restaurantDistanceComputer.getDistance(getDefaultLocation(), RESTAURANT_RESPONSE_2.getGeometry().getLocation()),
                "",
                convertRating(RESTAURANT_RESPONSE_2.getRating()),
                isRatingBarVisble(RESTAURANT_RESPONSE_2.getUserRatingsTotal()),
                null
            )
        );
        restaurantsItemViewStateList.add(
            new RestaurantsItemViewState(
                RESTAURANT_RESPONSE_3.getPlaceId(),
                RESTAURANT_RESPONSE_3.getName(),
                RESTAURANT_RESPONSE_3.getVicinity(),
                checkIfOpen(RESTAURANT_RESPONSE_3.getOpeningHours().getOpenNow()),
                restaurantDistanceComputer.getDistance(getDefaultLocation(), RESTAURANT_RESPONSE_3.getGeometry().getLocation()),
                "",
                convertRating(RESTAURANT_RESPONSE_3.getRating()),
                isRatingBarVisble(RESTAURANT_RESPONSE_3.getUserRatingsTotal()),
                null
            )
        );
        return new RestaurantsViewState(restaurantsItemViewStateList, false);
    }

    private String getPhotoRef(@Nullable RestaurantResponse response) {
        String ref;
        if (response != null && response.getPhotos() != null) {
            ref = response.getPhotos().get(0).getPhotoReference();
        } else {
            ref = null;
        }
        return ref;
    }

    @NonNull
    private String checkIfOpen(boolean isOpen) {
        String result;
        if (isOpen) {
            result = "Ouvert";
        } else {
            result = "Fermé";
        }
        return result;
    }

    private float convertRating(double restaurantRating) {
        return (float) Math.round(((restaurantRating * 3 / 5) / 0.5) * 0.5);
    }

    private boolean isRatingBarVisble(int userRatingsTotal) {
        return userRatingsTotal > 0;
    }
    // endregion
}