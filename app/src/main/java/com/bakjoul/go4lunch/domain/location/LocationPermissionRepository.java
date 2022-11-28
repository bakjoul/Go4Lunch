package com.bakjoul.go4lunch.domain.location;

import androidx.lifecycle.LiveData;

public interface LocationPermissionRepository {

    LiveData<Boolean> getLocationPermissionLiveData();

    void setLocationPermission(boolean granted);
}
