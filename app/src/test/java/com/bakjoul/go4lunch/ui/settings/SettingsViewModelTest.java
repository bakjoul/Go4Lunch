package com.bakjoul.go4lunch.ui.settings;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.settings.SetNotificationsPreferencesUseCase;
import com.bakjoul.go4lunch.domain.settings.SettingsRepository;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class SettingsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final SettingsRepository settingsRepository = Mockito.mock(SettingsRepository.class);

    private final SetNotificationsPreferencesUseCase setNotificationsPreferencesUseCase = Mockito.mock(SetNotificationsPreferencesUseCase.class);

    private final MutableLiveData<Boolean> areNotificationsEnabledLiveData = new MutableLiveData<>();

    private SettingsViewModel viewModel;

    @Before
    public void setUp() {
        areNotificationsEnabledLiveData.setValue(true);
        doReturn(areNotificationsEnabledLiveData).when(settingsRepository).areNotificationsEnabledLiveData();

        viewModel = new SettingsViewModel(settingsRepository, setNotificationsPreferencesUseCase);
    }

    @Test
    public void nominal_case() {
        // When
        SettingsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getSettingsViewStateLiveData());

        // Then
        assertEquals(getSettingsViewState(true), result);
    }

    @Test
    public void notificationsDisabled_case() {
        // Given
        areNotificationsEnabledLiveData.setValue(false);

        // When
        SettingsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getSettingsViewStateLiveData());

        // Then
        assertEquals(getSettingsViewState(false), result);
    }

    @Test
    public void onNotificationSwitchChanged_to_true() {
        // When
        viewModel.onNotificationSwitchChanged(true);

        // Then
        verify(setNotificationsPreferencesUseCase).invoke(true);
        verify(settingsRepository).setNotification(true);
    }

    @Test
    public void onNotificationSwitchChanged_to_false() {
        // When
        viewModel.onNotificationSwitchChanged(false);

        // Then
        verify(setNotificationsPreferencesUseCase).invoke(false);
        verify(settingsRepository).setNotification(false);
    }

    // region OUT
    @NonNull
    private SettingsViewState getSettingsViewState(boolean areNotificationsEnabled) {
        return new SettingsViewState(areNotificationsEnabled);
    }
    // endregion
}