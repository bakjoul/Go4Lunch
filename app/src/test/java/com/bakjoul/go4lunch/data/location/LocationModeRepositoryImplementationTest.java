package com.bakjoul.go4lunch.data.location;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LocationModeRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private LocationModeRepositoryImplementation locationModeRepositoryImplementation;

    @Before
    public void setUp() {
        locationModeRepositoryImplementation = new LocationModeRepositoryImplementation();
    }

    @Test
    public void setUserModeEnabled_boolean_should_update_user_mode_livedata_to_boolean() {
        // Given
        locationModeRepositoryImplementation.setUserModeEnabled(true);

        // When
        Boolean result = LiveDataTestUtil.getValueForTesting(locationModeRepositoryImplementation.isUserModeEnabledLiveData());

        // Then
        assertEquals(true, result);
    }

}