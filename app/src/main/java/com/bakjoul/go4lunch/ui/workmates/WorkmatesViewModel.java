package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

    private final MediatorLiveData<WorkmatesViewState> workmatesViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public WorkmatesViewModel(
        @NonNull WorkmateRepositoryImplementation workmateRepositoryImplementation
    ) {
        LiveData<List<WorkmateEntity>> availableWorkmatesLiveData = workmateRepositoryImplementation.getAvailableWorkmatesLiveData();
        LiveData<List<UserGoingToRestaurantEntity>> workmatesGoingToRestaurantLiveData = workmateRepositoryImplementation.getWorkmatesGoingToRestaurantLiveData();

        workmatesViewStateMediatorLiveData.addSource(availableWorkmatesLiveData, availableWorkmates ->
            combine(availableWorkmates, workmatesGoingToRestaurantLiveData.getValue())
        );
        workmatesViewStateMediatorLiveData.addSource(workmatesGoingToRestaurantLiveData, workmatesWithChoice ->
            combine(availableWorkmatesLiveData.getValue(), workmatesWithChoice)
        );
    }

    private void combine(
        @Nullable List<WorkmateEntity> availableWorkmates,
        @Nullable List<UserGoingToRestaurantEntity> workmatesGoingToRestaurant) {
        if (availableWorkmates == null || workmatesGoingToRestaurant == null) {
            return;
        }

        List<WorkmateItemViewState> workmateItemViewStateList = mapWorkmateResponse(availableWorkmates, workmatesGoingToRestaurant);
        workmatesViewStateMediatorLiveData.setValue(
            new WorkmatesViewState(workmateItemViewStateList, workmateItemViewStateList.isEmpty())
        );
    }

    public LiveData<WorkmatesViewState> getWorkmatesViewStateMediatorLiveData() {
        return workmatesViewStateMediatorLiveData;
    }

    @NonNull
    private List<WorkmateItemViewState> mapWorkmateResponse(
        @NonNull List<WorkmateEntity> availableWorkmates,
        @NonNull List<UserGoingToRestaurantEntity> workmateGoingToRestaurantList
    ) {
        List<WorkmateItemViewState> workmateItemViewStateList = new ArrayList<>();
        List<WorkmateItemViewState> workmateWithoutChosenRestaurantList = new ArrayList<>();
        for (WorkmateEntity workmateEntity : availableWorkmates) {
            String chosenRestaurantId = null;
            String chosenRestaurantName = null;
            for (UserGoingToRestaurantEntity userGoingToRestaurantEntity : workmateGoingToRestaurantList) {
                if (userGoingToRestaurantEntity.getId().equals(workmateEntity.getId())) {
                    chosenRestaurantId = userGoingToRestaurantEntity.getChosenRestaurantId();
                    chosenRestaurantName = userGoingToRestaurantEntity.getChosenRestaurantName();
                }
            }

            WorkmateItemViewState workmateItemViewState = new WorkmateItemViewState(
                workmateEntity.getId(),
                workmateEntity.getPhotoUrl(),
                workmateEntity.getUsername(),
                chosenRestaurantId,
                chosenRestaurantName
            );

            // For sorting, workmates going to restaurants will be displayed first
            if (workmateItemViewState.getChosenRestaurantId() == null) {
                workmateWithoutChosenRestaurantList.add(workmateItemViewState);
            } else {
                workmateItemViewStateList.add(workmateItemViewState);
            }
        }
        workmateItemViewStateList.addAll(workmateWithoutChosenRestaurantList);
        return workmateItemViewStateList;
    }
}
