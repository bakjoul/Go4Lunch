package com.bakjoul.go4lunch.data.autocomplete;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.data.api.GoogleApis;
import com.bakjoul.go4lunch.data.autocomplete.model.AutocompleteResponse;
import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutocompleteRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GoogleApis googleApis = Mockito.mock(GoogleApis.class);

    private AutocompleteRepositoryImplementation autocompleteRepositoryImplementation;

    @Before
    public void setUp() {
        autocompleteRepositoryImplementation = new AutocompleteRepositoryImplementation(googleApis);
    }

    @Test
    public void nominal_case() {
        Location location = Mockito.mock(Location.class);
        Call<AutocompleteResponse> mockedCall = Mockito.mock(Call.class);
        Mockito.doReturn(mockedCall).when(googleApis).getRestaurantAutocomplete(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        Mockito.doAnswer(invocation -> {
            Callback<AutocompleteResponse> callback = invocation.getArgument(0);
            callback.onResponse(
                mockedCall,
                Response.success(getExpectedAutocompleteResponse())
            );
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        // When
        List<PredictionResponse> result = LiveDataTestUtil.getValueForTesting(autocompleteRepositoryImplementation.getPredictionsLiveData("test", location));

        // Then
        assertEquals(getExpectedPredictionResponseList(), result);

    }

    @Test
    public void setUserSearch_should_update_userSearchLiveData() {
        // Given
        autocompleteRepositoryImplementation.setUserSearch("test");

        // When
        String result = LiveDataTestUtil.getValueForTesting(autocompleteRepositoryImplementation.getUserSearchLiveData());

        // Then
        assertEquals("test", result);
    }

    // region IN
    @NonNull
    private AutocompleteResponse getExpectedAutocompleteResponse() {
        return new AutocompleteResponse(
            getExpectedPredictionResponseList(),
            "OK"
        );
    }
    // endregion IN

    // region OUT
    @NonNull
    private List<PredictionResponse> getExpectedPredictionResponseList() {
        return Collections.singletonList(new PredictionResponse(
            "fakeDescription",
            null,
            "fakePlaceId",
            "fakeReference",
            null,
            null,
            Collections.singletonList("restaurant")
        ));
    }
    // endregion OUT
}