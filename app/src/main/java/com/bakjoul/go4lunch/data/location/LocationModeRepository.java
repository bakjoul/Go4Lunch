package com.bakjoul.go4lunch.data.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationModeRepository {

    @Inject
    public LocationModeRepository() {
    }

    private final MutableLiveData<Boolean> isUserModeEnabledMutableLiveData = new MutableLiveData<>(false);

    public LiveData<Boolean> isUserModeEnabledLiveData() {
        return isUserModeEnabledMutableLiveData;
    }

    public void setModeUserEnabled(boolean enabled) {
        isUserModeEnabledMutableLiveData.setValue(enabled);
    }
}
