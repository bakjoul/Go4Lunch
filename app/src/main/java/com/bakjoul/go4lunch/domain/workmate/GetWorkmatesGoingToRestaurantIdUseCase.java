package com.bakjoul.go4lunch.domain.workmate;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetWorkmatesGoingToRestaurantIdUseCase {

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @NonNull
    private final WorkmateRepository workmateRepository;

    @Inject
    public GetWorkmatesGoingToRestaurantIdUseCase(
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase,
        @NonNull WorkmateRepository workmateRepository
    ) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.workmateRepository = workmateRepository;
    }

    public LiveData<List<UserGoingToRestaurantEntity>> invoke(String restaurantId) {
        LoggedUserEntity currentUser = getCurrentUserUseCase.invoke();

        if (currentUser == null) {
            return new MutableLiveData<>(null);
        } else {
            return Transformations.map(workmateRepository.getWorkmatesGoingToRestaurantIdLiveData(restaurantId), workmateEntities -> {
                List<UserGoingToRestaurantEntity> filteredWorkmateEntities = new ArrayList<>(workmateEntities.size());

                for (UserGoingToRestaurantEntity workmateEntity : workmateEntities) {
                    if (!workmateEntity.getId().equals(currentUser.getId())) {
                        filteredWorkmateEntities.add(workmateEntity);
                    }
                }

                return filteredWorkmateEntities;
            });
        }
    }
}
