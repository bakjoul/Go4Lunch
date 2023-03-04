package com.bakjoul.go4lunch.data.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class BooleanSharedPreferencesLiveData extends MutableLiveData<Boolean> {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.OnSharedPreferenceChangeListener listener;

    public BooleanSharedPreferencesLiveData(
        @NonNull @ApplicationContext Context context,
        @NonNull String prefFileName,
        @NonNull String key
    ) {
        super();

        sharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        setValue(sharedPreferences.getBoolean(key, true));
        listener = (sharedPreferences, changedKey) -> {
            if (key.equals(changedKey)) {
                setValue(sharedPreferences.getBoolean(key, true));
            }
        };
    }

    @Override
    protected void onActive() {
        super.onActive();

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
