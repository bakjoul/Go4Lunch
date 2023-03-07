package com.bakjoul.go4lunch.domain.settings;

import androidx.annotation.NonNull;

import javax.inject.Inject;

public class AreNotificationsEnabledUseCase {

    @NonNull
    private final SettingsRepository settingsRepository;

    @Inject
    public AreNotificationsEnabledUseCase(
        @NonNull SettingsRepository settingsRepository
    ) {
        this.settingsRepository = settingsRepository;
    }

    public boolean invoke() {
        return settingsRepository.areNotificationsEnabled();
    }
}
