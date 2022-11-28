package com.bakjoul.go4lunch.domain.location;

import androidx.lifecycle.LiveData;

public interface LocationModeRepository {

    LiveData<Boolean> isUserModeEnabledLiveData();

    void setModeUserEnabled(boolean enabled);
}
