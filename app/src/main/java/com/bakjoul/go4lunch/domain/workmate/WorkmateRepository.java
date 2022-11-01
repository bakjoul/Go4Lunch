package com.bakjoul.go4lunch.domain.workmate;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface WorkmateRepository {

    LiveData<List<WorkmateEntity>> getAllWorkmatesForPlaceId(String placeId);

}
