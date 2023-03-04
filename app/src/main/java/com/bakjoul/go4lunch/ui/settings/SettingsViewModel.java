package com.bakjoul.go4lunch.ui.settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.domain.settings.SettingsRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    @NonNull
    private final SettingsRepository settingsRepository;

    @Inject
    public SettingsViewModel(@NonNull SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public LiveData<SettingsViewState> getSettingsViewStateLiveData() {
        return Transformations.switchMap(
            settingsRepository.isNotificationEnabled(),
            isNotificationEnabled -> {
                if (isNotificationEnabled) {
                    return new MutableLiveData<>(new SettingsViewState(true));
                } else {
                    return new MutableLiveData<>(new SettingsViewState(false));
                }
            }
        );
    }

    public void onNotificationSwitchChanged(boolean isChecked) {
        settingsRepository.setNotification(isChecked);
    }
}
