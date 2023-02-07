package com.bakjoul.go4lunch.data.location;

import static org.junit.Assert.assertEquals;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class MapLocationRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MapLocationRepositoryImplementation mapLocationRepositoryImplementation;

    @Before
    public void setUp() {
        mapLocationRepositoryImplementation = new MapLocationRepositoryImplementation();
    }

    @Test
    public void setCurrentMapLocation_should_update_map_location_livedata() {
        // Given
        Location location = Mockito.mock(Location.class);
        mapLocationRepositoryImplementation.setCurrentMapLocation(location);

        // When
        Location result = LiveDataTestUtil.getValueForTesting(mapLocationRepositoryImplementation.getCurrentMapLocationLiveData());

        // Then
        assertEquals(location, result);
    }

}