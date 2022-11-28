package com.bakjoul.go4lunch.data.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.location.LocationModeRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocationModeRepositoryImplementation implements LocationModeRepository {

    private final MutableLiveData<Boolean> isUserModeEnabledMutableLiveData = new MutableLiveData<>(false);

    @Inject
    public LocationModeRepositoryImplementation() {
    }

    @Override
    public LiveData<Boolean> isUserModeEnabledLiveData() {
        return isUserModeEnabledMutableLiveData;
    }

    @Override
    public boolean isUserModeEnabled() {
        //noinspection ConstantConditions This MutableLiveData always has a value
        return isUserModeEnabledMutableLiveData.getValue();
    }

    @Override
    public void setModeUserEnabled(boolean enabled) {
        isUserModeEnabledMutableLiveData.setValue(enabled);
    }
}
