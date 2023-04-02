package com.bakjoul.go4lunch.domain.autocomplete;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;
import com.bakjoul.go4lunch.domain.location.GetUserPositionUseCase;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetAutocompletePredictionsUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final AutocompleteRepository autocompleteRepository = Mockito.mock(AutocompleteRepository.class);

    private final GetUserPositionUseCase getUserPositionUseCase = Mockito.mock(GetUserPositionUseCase.class);

    private final Location location = Mockito.mock(Location.class);

    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>(location);

    private final MutableLiveData<List<PredictionResponse>> predictions = new MutableLiveData<>();

    private GetAutocompletePredictionsUseCase getAutocompletePredictionsUseCase;

    @Before
    public void setUp() {
        doReturn(locationLiveData).when(getUserPositionUseCase).invoke();
        doReturn(predictions).when(autocompleteRepository).getPredictionsLiveData(anyString(), any(Location.class));

        getAutocompletePredictionsUseCase = new GetAutocompletePredictionsUseCase(autocompleteRepository, getUserPositionUseCase);
    }

    @Test
    public void empty_search_should_return_empty_list() {
        // Given
        String userSearch = null;

        // When
        List<PredictionResponse> result = LiveDataTestUtil.getValueForTesting(getAutocompletePredictionsUseCase.invoke(userSearch));

        // Then
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void invalid_search_length_should_return_empty_list() {
        // Given
        String userSearch = "te";

        // When
        List<PredictionResponse> result = LiveDataTestUtil.getValueForTesting(getAutocompletePredictionsUseCase.invoke(userSearch));

        // Then
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void nominal_case() {
        // Given
        String userSearch = "test";
        predictions.setValue(getDefaultPredictions());

        // When
        List<PredictionResponse> result = LiveDataTestUtil.getValueForTesting(getAutocompletePredictionsUseCase.invoke(userSearch));

        // Then
        assertEquals(getDefaultPredictions(), result);
    }

    @Test
    public void location_null_should_return_empty_list() {
        // Given
        String userSearch = "test";
        locationLiveData.setValue(null);

        // When
        List<PredictionResponse> result = LiveDataTestUtil.getValueForTesting(getAutocompletePredictionsUseCase.invoke(userSearch));

        // Then
        assertEquals(new ArrayList<>(), result);
    }

    @NonNull
    private List<PredictionResponse> getDefaultPredictions() {
        return Collections.singletonList(
            new PredictionResponse(
                "fakeDescription",
                null,
                "fakePlaceId",
                "fakeReference",
                null,
                null,
                Collections.singletonList("restaurant"))
        );
    }
}
