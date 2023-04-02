package com.bakjoul.go4lunch.domain.workmate;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetAvailableWorkmatesUseCase {

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    @NonNull
    private final WorkmateRepository workmateRepository;

    @Inject
    public GetAvailableWorkmatesUseCase(
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase,
        @NonNull WorkmateRepository workmateRepository
    ) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.workmateRepository = workmateRepository;
    }

    public LiveData<List<WorkmateEntity>> invoke() {
        LoggedUserEntity currentUser = getCurrentUserUseCase.invoke();

        if (currentUser == null) {
            return new MutableLiveData<>(null);
        } else {
            return Transformations.map(workmateRepository.getAvailableWorkmatesLiveData(), workmateEntities -> {
                List<WorkmateEntity> filteredWorkmateEntities = new ArrayList<>(workmateEntities.size());

                for (WorkmateEntity workmateEntity : workmateEntities) {
                    if (!workmateEntity.getId().equals(currentUser.getId())) {
                        filteredWorkmateEntities.add(workmateEntity);
                    }
                }

                return filteredWorkmateEntities;
            });
        }
    }
}
