package com.bakjoul.go4lunch.data.restaurants;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.restaurants.model.NearbySearchResponse;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantResponse;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantResponseWrapper;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GoogleApis googleApis = Mockito.mock(GoogleApis.class);

    //private final LruCache<NearbySearchQuery, NearbySearchResponse> lruCache = Mockito.mock(LruCache.class);

    private RestaurantRepositoryImplementation restaurantRepositoryImplementation;

    @Before
    public void setUp() {
        restaurantRepositoryImplementation = new RestaurantRepositoryImplementation(googleApis);
    }

    @Test
    public void nominal_case() {
        // Given
        Location location = Mockito.mock(Location.class);
        Call<NearbySearchResponse> mockedCall = Mockito.mock(Call.class);
        doReturn(mockedCall).when(googleApis).getNearbyRestaurants(anyString(), anyString(), anyString(), anyString());
        Mockito.doAnswer(invocation -> {
            Callback<NearbySearchResponse> callback = invocation.getArgument(0);
            callback.onResponse(mockedCall, Response.success(getDefaultNearbySearchResponse()));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        // When
        RestaurantResponseWrapper result = LiveDataTestUtil.getValueForTesting(restaurantRepositoryImplementation.getNearbyRestaurants(location));

        // Then
        assertEquals(getExpectedResponseWrapper(), result);
    }

/*    @Test
    public void existing_response_for_given_query_should_be_exposed() {
        // Given
        Location location = Mockito.mock(Location.class);
        doReturn(getDefaultNearbySearchResponse()).when(lruCache).get(any(NearbySearchQuery.class));

        // When
        RestaurantResponseWrapper result = LiveDataTestUtil.getValueForTesting(restaurantRepositoryImplementation.getNearbyRestaurants(location));

        // Then
        assertEquals(getExpectedResponseWrapper(), result);
    }*/

    /*@Test
    public void verify_lru_cache_state() {
        // Given
        Location location = Mockito.mock(Location.class);
        Call<NearbySearchResponse> mockedCall = Mockito.mock(Call.class);
        doReturn(mockedCall).when(googleApis).getNearbyRestaurants(anyString(), anyString(), anyString(), anyString());
        Mockito.doAnswer(invocation -> {
            Callback<NearbySearchResponse> callback = invocation.getArgument(0);
            callback.onResponse(mockedCall, Response.success(getDefaultNearbySearchResponse()));
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));
        LiveDataTestUtil.getValueForTesting(restaurantRepositoryImplementation.getNearbyRestaurants(location)); // Passe du When au Given

        // When
        RestaurantResponseWrapper result = LiveDataTestUtil.getValueForTesting(restaurantRepositoryImplementation.getNearbyRestaurants(location));

        // Then
        assertEquals(getExpectedResponseWrapper(), result);
        verify(googleApis).getNearbyRestaurants(anyString(), anyString(), anyString(), anyString()); // We call the function twice but we verify we called server only once
    }*/

    // region IN
    @NonNull
    private NearbySearchResponse getDefaultNearbySearchResponse() {
        return new NearbySearchResponse(
            Collections.singletonList(
                new RestaurantResponse(
                    "RESTAURANT_ID_1",
                    "RESTAURANT_NAME_1",
                    "RESTAURANT_VICINITY_1",
                    null,
                    null,
                    5.0,
                    null,
                    "OPERATIONAL",
                    5
                )
            ),
            "OK"
        );
    }
    // endregion IN

    // region OUT
    @NonNull
    private RestaurantResponseWrapper getExpectedResponseWrapper() {
        return new RestaurantResponseWrapper(
            getDefaultNearbySearchResponse(),
            RestaurantResponseWrapper.State.SUCCESS
        );
    }
    // endregion
}