package com.bakjoul.go4lunch.domain.location;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class GetUserPositionUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final LocationModeRepository locationModeRepository = Mockito.mock(LocationModeRepository.class);

    private final GpsLocationRepository gpsLocationRepository = Mockito.mock(GpsLocationRepository.class);

    private final MapLocationRepository mapLocationRepository = Mockito.mock(MapLocationRepository.class);

    private final Location location = Mockito.mock(Location.class);

    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>(location);

    private final MutableLiveData<Boolean> isUserModeEnabledLiveData = new MutableLiveData<>();

    private GetUserPositionUseCase getUserPositionUseCase;

    @Before
    public void setUp() {
        doReturn(isUserModeEnabledLiveData).when(locationModeRepository).isUserModeEnabledLiveData();
        doReturn(locationLiveData).when(mapLocationRepository).getCurrentMapLocationLiveData();
        doReturn(locationLiveData).when(gpsLocationRepository).getCurrentLocationLiveData();

        getUserPositionUseCase = new GetUserPositionUseCase(locationModeRepository, gpsLocationRepository, mapLocationRepository);
    }

    @Test
    public void userModeEnabled_should_return_current_map_location_livedata() {
        // Given
        isUserModeEnabledLiveData.setValue(true);

        // When
        Location result = LiveDataTestUtil.getValueForTesting(getUserPositionUseCase.invoke());

        // Then
        assertEquals(location, result);
        verify(mapLocationRepository).getCurrentMapLocationLiveData();
        verifyNoMoreInteractions(mapLocationRepository);
        verifyNoInteractions(gpsLocationRepository);
    }

    @Test
    public void userModeDisabled_should_return_current_gps_location_livedata() {
        // Given
        isUserModeEnabledLiveData.setValue(false);

        // When
        Location result = LiveDataTestUtil.getValueForTesting(getUserPositionUseCase.invoke());

        // Then
        assertEquals(location, result);
        verify(gpsLocationRepository).getCurrentLocationLiveData();
        verifyNoMoreInteractions(gpsLocationRepository);
        verifyNoInteractions(mapLocationRepository);

    }
}