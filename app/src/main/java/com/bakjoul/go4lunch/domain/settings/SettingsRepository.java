package com.bakjoul.go4lunch.domain.settings;

import androidx.lifecycle.LiveData;

public interface SettingsRepository {

    void setNotification(boolean isEnabled);

    boolean areNotificationsEnabled();

    LiveData<Boolean> areNotificationsEnabledLiveData();
}
