package com.bakjoul.go4lunch.data.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.utils.LiveDataTestUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

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
    public void startLocationUpdates_should_expose_new_location() {
        // Given
        ArgumentCaptor<LocationCallback> locationCallbackArgumentCaptor = ArgumentCaptor.forClass(LocationCallback.class);
        LocationResult locationResult = Mockito.mock(LocationResult.class);
        Location location = Mockito.mock(Location.class);
        Mockito.doReturn(location).when(locationResult).getLastLocation();
        gpsLocationRepositoryImplementation.startLocationUpdates();

        // When
        LiveData<Location> liveData = gpsLocationRepositoryImplementation.getCurrentLocationLiveData();
        liveData.observeForever(t -> {
        });

        Mockito.verify(fusedLocationProviderClient).requestLocationUpdates(
            any(),
            locationCallbackArgumentCaptor.capture(),
            any()
        );
        locationCallbackArgumentCaptor.getValue().onLocationResult(locationResult);

        Boolean isPermissionGranted = LiveDataTestUtil.getValueForTesting(gpsLocationRepositoryImplementation.getIsLocationPermissionAllowedLiveData());

        // Then
        assertEquals(location, liveData.getValue());
        assertTrue(isPermissionGranted);
    }

    @Test
    public void startLocationUpdates_should_expose_new_location_bis() {
        // Given
        LocationResult locationResult = Mockito.mock(LocationResult.class);
        Location location = Mockito.mock(Location.class);
        Mockito.doReturn(location).when(locationResult).getLastLocation();
        Mockito.doAnswer((Answer<Task<Void>>) invocation -> {
            LocationCallback callback = invocation.getArgument(1);
            callback.onLocationResult(locationResult);
            return null;
        }).when(fusedLocationProviderClient).requestLocationUpdates(any(), any(LocationCallback.class), any());
        gpsLocationRepositoryImplementation.startLocationUpdates();

        // When
        Location result = LiveDataTestUtil.getValueForTesting(gpsLocationRepositoryImplementation.getCurrentLocationLiveData());
        Boolean isPermissionGranted = LiveDataTestUtil.getValueForTesting(gpsLocationRepositoryImplementation.getIsLocationPermissionAllowedLiveData());

        // Then
        assertEquals(location, result);
        assertTrue(isPermissionGranted);
    }

    @Test
    public void stopLocationUpdates_should_expose_location_null() {
        // Given
        LocationResult locationResult = Mockito.mock(LocationResult.class);
        Location location = Mockito.mock(Location.class);
        Mockito.doReturn(location).when(locationResult).getLastLocation();
        Mockito.doAnswer((Answer<Task<Void>>) invocation -> {
            LocationCallback callback = invocation.getArgument(0);
            callback.onLocationResult(locationResult);
            return null;
        }).when(fusedLocationProviderClient).removeLocationUpdates(any(LocationCallback.class));
        gpsLocationRepositoryImplementation.stopLocationUpdates();

        // When
        Location result = LiveDataTestUtil.getValueForTesting(gpsLocationRepositoryImplementation.getCurrentLocationLiveData());
        Boolean isPermissionGranted = LiveDataTestUtil.getValueForTesting(gpsLocationRepositoryImplementation.getIsLocationPermissionAllowedLiveData());

        // Then
        assertEquals(location, result);
        assertFalse(isPermissionGranted);
    }
}