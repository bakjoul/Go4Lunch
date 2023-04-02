package com.bakjoul.go4lunch.domain.settings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class AreNotificationsEnabledUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final SettingsRepository settingsRepository = Mockito.mock(SettingsRepository.class);

    private AreNotificationsEnabledUseCase areNotificationsEnabledUseCase;

    @Before
    public void setUp() {
        areNotificationsEnabledUseCase = new AreNotificationsEnabledUseCase(settingsRepository);
    }

    @Test
    public void invoke_should_return_true() {
        // Given
        doReturn(true).when(settingsRepository).areNotificationsEnabled();

        // When
        boolean result = areNotificationsEnabledUseCase.invoke();

        // Then
        assertTrue(result);
    }

    @Test
    public void invoke_should_return_false() {
        // Given
        doReturn(false).when(settingsRepository).areNotificationsEnabled();

        // When
        boolean result = areNotificationsEnabledUseCase.invoke();

        // Then
        assertFalse(result);
    }
}
