package com.bakjoul.go4lunch.ui.settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.domain.settings.SetNotificationsPreferencesUseCase;
import com.bakjoul.go4lunch.domain.settings.SettingsRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final SetNotificationsPreferencesUseCase setNotificationsPreferencesUseCase;

    @Inject
    public SettingsViewModel(
        @NonNull SettingsRepository settingsRepository,
        @NonNull SetNotificationsPreferencesUseCase setNotificationsPreferencesUseCase
    ) {
        this.settingsRepository = settingsRepository;
        this.setNotificationsPreferencesUseCase = setNotificationsPreferencesUseCase;
    }

    public LiveData<SettingsViewState> getSettingsViewStateLiveData() {
        //noinspection Convert2MethodRef
        return Transformations.map(
            settingsRepository.areNotificationsEnabledLiveData(),
            areNotificationsEnabled -> new SettingsViewState(areNotificationsEnabled)
        );
    }

    public void onNotificationSwitchChanged(boolean isChecked) {
        setNotificationsPreferencesUseCase.invoke(isChecked);
    }
}
