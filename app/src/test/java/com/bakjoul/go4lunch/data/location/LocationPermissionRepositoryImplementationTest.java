package com.bakjoul.go4lunch.data.location;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.domain.location.IsLocationPermissionGrantedUseCase;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class LocationPermissionRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final IsLocationPermissionGrantedUseCase isLocationPermissionGrantedUseCase = Mockito.mock(IsLocationPermissionGrantedUseCase.class);

    private LocationPermissionRepositoryImplementation locationPermissionRepositoryImplementation;

/*    @Before
    public void setUp() {
        locationPermissionRepositoryImplementation = new LocationPermissionRepositoryImplementation(isLocationPermissionGrantedUseCase);
    }*/

    @Test
    public void location_permission_granted_should_set_permission_livedata_to_true() {
        // Given
        doReturn(true).when(isLocationPermissionGrantedUseCase).invoke();
        initLocationPermissionRepositoryImplementation();

        // When
        Boolean result = LiveDataTestUtil.getValueForTesting(locationPermissionRepositoryImplementation.getLocationPermissionLiveData());

        // Then
        assertTrue(result);
    }

    @Test
    public void location_permission_denied_should_set_permission_livedata_to_false() {
        // Given
        doReturn(false).when(isLocationPermissionGrantedUseCase).invoke();
        initLocationPermissionRepositoryImplementation();

        // When
        Boolean result = LiveDataTestUtil.getValueForTesting(locationPermissionRepositoryImplementation.getLocationPermissionLiveData());

        // Then
        assertFalse(result);
    }

    @Test
    public void setLocationPermission_should_update_permission_livedata() {
        // Given
        doReturn(false).when(isLocationPermissionGrantedUseCase).invoke();
        initLocationPermissionRepositoryImplementation();
        locationPermissionRepositoryImplementation.setLocationPermission(true);

        // When
        Boolean result = LiveDataTestUtil.getValueForTesting(locationPermissionRepositoryImplementation.getLocationPermissionLiveData());

        // Then
        assertTrue(result);
    }

    // region IN
    private void initLocationPermissionRepositoryImplementation() {
        locationPermissionRepositoryImplementation = new LocationPermissionRepositoryImplementation(isLocationPermissionGrantedUseCase);
    }
    // endregion IN

}