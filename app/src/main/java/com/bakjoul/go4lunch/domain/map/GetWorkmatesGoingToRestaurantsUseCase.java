package com.bakjoul.go4lunch.domain.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;

import java.util.Collection;
import java.util.HashSet;

import javax.inject.Inject;

public class GetWorkmatesGoingToRestaurantsUseCase {

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @NonNull
    private final WorkmateRepository workmateRepository;

    @Inject
    public GetWorkmatesGoingToRestaurantsUseCase(
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase,
        @NonNull WorkmateRepository workmateRepository
    ) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.workmateRepository = workmateRepository;
    }

    public LiveData<Collection<String>> invoke() {
        LoggedUserEntity currentUser = getCurrentUserUseCase.invoke();

        if (currentUser == null) {
            return new MutableLiveData<>(null);
        } else {
            return Transformations.map(workmateRepository.getWorkmatesGoingToRestaurantsLiveData(), workmateEntities -> {
                Collection<String> workmatesGoingToRestaurants = new HashSet<>(workmateEntities.size());

                for (UserGoingToRestaurantEntity workmateEntity : workmateEntities) {
                    if (!workmateEntity.getId().equals(currentUser.getId())) {
                        workmatesGoingToRestaurants.add(workmateEntity.getChosenRestaurantId());
                    }
                }

                return workmatesGoingToRestaurants;
            });
        }
    }
}
