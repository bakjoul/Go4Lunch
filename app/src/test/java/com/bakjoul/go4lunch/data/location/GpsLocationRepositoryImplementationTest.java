package com.bakjoul.go4lunch.data.location;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.utils.LiveDataTestUtil;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class GpsLocationRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final FusedLocationProviderClient fusedLocationProviderClient = Mockito.mock(FusedLocationProviderClient.class);

    private GpsLocationRepositoryImplementation gpsLocationRepositoryImplementation;

    @Before
    public void setUp() {
        gpsLocationRepositoryImplementation = new GpsLocationRepositoryImplementation(fusedLocationProviderClient);
    }

    @Test
    public void startLocationUpdates_should_allow_location_permission() {
        // Given
        gpsLocationRepositoryImplementation.startLocationUpdates();

        // When
        Boolean result = LiveDataTestUtil.getValueForTesting(gpsLocationRepositoryImplementation.getIsLocationPermissionAllowedLiveData());

        // Then
        assertTrue(result);
    }

    @Test
    public void stopLocationUpdates_should_disallow_location_permission() {
        // Given
        gpsLocationRepositoryImplementation.stopLocationUpdates();

        // When
        Boolean result = LiveDataTestUtil.getValueForTesting(gpsLocationRepositoryImplementation.getIsLocationPermissionAllowedLiveData());

        // Then
        assertFalse(result);
    }
}