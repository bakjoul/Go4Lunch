package com.bakjoul.go4lunch.domain.location;

import androidx.lifecycle.LiveData;

public interface LocationModeRepository {

    LiveData<Boolean> isUserModeEnabledLiveData();

    boolean isUserModeEnabled();

    void setModeUserEnabled(boolean enabled);
}
