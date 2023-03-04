package com.bakjoul.go4lunch.domain.settings;

import androidx.lifecycle.LiveData;

public interface SettingsRepository {

    void setNotification(boolean isEnabled);

    LiveData<Boolean> isNotificationEnabled();
}
