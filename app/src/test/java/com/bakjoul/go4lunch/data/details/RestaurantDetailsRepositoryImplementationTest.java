package com.bakjoul.go4lunch.data.details;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.details.model.DetailsResponse;
import com.bakjoul.go4lunch.data.details.model.RestaurantDetailsResponse;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GoogleApis googleApis = Mockito.mock(GoogleApis.class);

    private RestaurantDetailsRepositoryImplementation restaurantDetailsRepositoryImplementation;

    @Before
    public void setUp() {
        restaurantDetailsRepositoryImplementation = new RestaurantDetailsRepositoryImplementation(googleApis);
    }

    @Test
    public void nominal_case() {
        Call<DetailsResponse> mockedCall = Mockito.mock(Call.class);
        Mockito.doReturn(mockedCall).when(googleApis).getRestaurantDetails(anyString(), anyString());
        Mockito.doAnswer(invocation -> {
            Callback<DetailsResponse> callback = invocation.getArgument(0);
            callback.onResponse(
                mockedCall,
                Response.success(getExpectedDetailsResponse())
            );
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        // When
        DetailsResponse result = LiveDataTestUtil.getValueForTesting(restaurantDetailsRepositoryImplementation.getDetailsResponse("restaurantId"));

        // Then
        assertEquals(getExpectedDetailsResponse(), result);
    }

    // region IN
    @NonNull
    private RestaurantDetailsResponse getExpectedRestaurantDetailsResponse() {
        return new RestaurantDetailsResponse(
            "RESTAURANT_DETAILS_PLACE_ID",
            "RESTAURANT_DETAILS_NAME",
            5.0,
            50,
            "RESTAURANT_DETAILS_FORMATTED_ADDRESS",
            null,
            null,
            "RESTAURANT_DETAILS_FORMATTED_PHONE_NUMBER",
            "RESTAURANT_DETAILS_WEBSITE_URL"
        );
    }
    // endregion IN

    // region OUT
    @NonNull
    private DetailsResponse getExpectedDetailsResponse() {
        return new DetailsResponse(getExpectedRestaurantDetailsResponse(), "OK");
    }
    // endregion OUT
}