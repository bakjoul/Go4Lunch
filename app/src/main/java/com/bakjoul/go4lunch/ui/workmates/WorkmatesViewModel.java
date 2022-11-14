package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

    private final MediatorLiveData<WorkmatesViewState> workmatesViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public WorkmatesViewModel(@NonNull WorkmateRepositoryImplementation workmateRepositoryImplementation) {
        LiveData<List<WorkmateEntity>> availableWorkmatesLiveData = workmateRepositoryImplementation.getAvailableWorkmatesLiveData();
        LiveData<Map<String, Map<String, Object>>> workmatesWithChoiceLiveData = workmateRepositoryImplementation.getWorkmatesWithChoiceLiveData();

        workmatesViewStateMediatorLiveData.addSource(availableWorkmatesLiveData, availableWorkmates ->
            combine(availableWorkmates, workmatesWithChoiceLiveData.getValue())
        );
        workmatesViewStateMediatorLiveData.addSource(workmatesWithChoiceLiveData, workmatesWithChoice ->
            combine(availableWorkmatesLiveData.getValue(), workmatesWithChoice)
        );
    }

    private void combine(
        @Nullable List<WorkmateEntity> availableWorkmates,
        @Nullable Map<String, Map<String, Object>> workmatesWithChoice) {
        if (availableWorkmates == null || workmatesWithChoice == null) {
            return;
        }

        List<WorkmateItemViewState> workmateItemViewStateList = mapWorkmateResponse(availableWorkmates, workmatesWithChoice);
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
        @NonNull Map<String, Map<String, Object>> workmatesWithChoice
    ) {
        List<WorkmateItemViewState> workmateItemViewStateList = new ArrayList<>();
        for (WorkmateEntity workmateEntity : availableWorkmates) {
            String chosenRestaurantId = null;
            String chosenRestaurantName = null;
            if (workmatesWithChoice.containsKey(workmateEntity.getId())) {
                chosenRestaurantId = workmatesWithChoice.get(workmateEntity.getId()).keySet().toArray()[0].toString();
                chosenRestaurantName = workmatesWithChoice.get(workmateEntity.getId()).values().toArray()[0].toString();
            }
            WorkmateItemViewState workmateItemViewState = new WorkmateItemViewState(
                workmateEntity.getId(),
                workmateEntity.getPhotoUrl(),
                workmateEntity.getUsername(),
                chosenRestaurantId,
                chosenRestaurantName
            );
            workmateItemViewStateList.add(workmateItemViewState);
        }
        return workmateItemViewStateList;
    }
}
