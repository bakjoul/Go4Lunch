package com.bakjoul.go4lunch.domain.location;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;

import android.Manifest;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class IsLocationPermissionGrantedUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final Context context = Mockito.mock(Context.class);

    private final PermissionUtil permissionUtil = Mockito.mock(PermissionUtil.class);

    private IsLocationPermissionGrantedUseCase isLocationPermissionGrantedUseCase;

    @Before
    public void setUp() {
        isLocationPermissionGrantedUseCase = new IsLocationPermissionGrantedUseCase(context, permissionUtil);
    }

    @Test
    public void location_permission_granted_should_return_true() {
        // Given
        doReturn(true).when(permissionUtil).checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        // When
        boolean result = isLocationPermissionGrantedUseCase.invoke();

        // Then
        assertTrue(result);
    }

    @Test
    public void location_permission_denied_should_return_false() {
        // Given
        doReturn(false).when(permissionUtil).checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        // When
        boolean result = isLocationPermissionGrantedUseCase.invoke();

        // Then
        assertFalse(result);
    }
}
