package com.bakjoul.go4lunch.data.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.utils.BooleanSharedPreferencesLiveData;
import com.bakjoul.go4lunch.domain.settings.SettingsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SettingsRepositoryImplementation implements SettingsRepository {

    private static final String PREFERENCES_FILENAME = "settings";
    private static final String KEY_LUNCH_REMINDER = "lunchReminder";

    private final Context context;
    private final SharedPreferences sharedPreferences;

    @Inject
    public SettingsRepositoryImplementation(@NonNull @ApplicationContext Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);
    }

    @Override
    public void setNotification(boolean isEnabled) {
        sharedPreferences.edit().putBoolean(KEY_LUNCH_REMINDER, isEnabled).apply();
    }

    @Override
    public LiveData<Boolean> isNotificationEnabled() {
        return new BooleanSharedPreferencesLiveData(context, PREFERENCES_FILENAME, KEY_LUNCH_REMINDER);
    }
}
