package com.bakjoul.go4lunch.domain.restaurants;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class GetRestaurantsAttendanceUseCase {

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @NonNull
    private final WorkmateRepository workmateRepository;

    @Inject
    public GetRestaurantsAttendanceUseCase(
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase,
        @NonNull WorkmateRepository workmateRepository
    ) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.workmateRepository = workmateRepository;
    }

    public LiveData<Map<String, Integer>> invoke() {
        LoggedUserEntity currentUser = getCurrentUserUseCase.invoke();

        if (currentUser == null) {
            return new MutableLiveData<>(null);
        } else {
            return Transformations.map(workmateRepository.getWorkmatesGoingToRestaurantsLiveData(), workmates -> {
                Map<String, Integer> restaurantsAttendance = new HashMap<>();

                for (UserGoingToRestaurantEntity workmate : workmates) {
                    if (!workmate.getId().equals(currentUser.getId())) {
                        restaurantsAttendance.merge(workmate.getChosenRestaurantId(), 1, Integer::sum);
                    }
                }

                return restaurantsAttendance;
            });
        }
    }
}
