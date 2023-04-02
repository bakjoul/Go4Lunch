package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.workmate.GetAvailableWorkmatesUseCase;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

    private final SingleLiveEvent<Boolean> isUserSearchUnmatchedSingleLiveEvent = new SingleLiveEvent<>();

    private final MediatorLiveData<WorkmatesViewState> workmatesViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public WorkmatesViewModel(
        @NonNull WorkmateRepository workmateRepository,
        @NonNull GetAvailableWorkmatesUseCase getAvailableWorkmatesUseCase,
        @NonNull AutocompleteRepository autocompleteRepository
    ) {
        LiveData<List<WorkmateEntity>> availableWorkmatesLiveData = getAvailableWorkmatesUseCase.invoke();
        LiveData<List<UserGoingToRestaurantEntity>> workmatesGoingToRestaurantsLiveData = workmateRepository.getWorkmatesGoingToRestaurantsLiveData();
        LiveData<String> userSearchLiveData = autocompleteRepository.getUserSearchLiveData();

        workmatesViewStateMediatorLiveData.addSource(availableWorkmatesLiveData, availableWorkmates ->
            combine(availableWorkmates, workmatesGoingToRestaurantsLiveData.getValue(), userSearchLiveData.getValue())
        );
        workmatesViewStateMediatorLiveData.addSource(workmatesGoingToRestaurantsLiveData, workmatesWithChoice ->
            combine(availableWorkmatesLiveData.getValue(), workmatesWithChoice, userSearchLiveData.getValue())
        );
        workmatesViewStateMediatorLiveData.addSource(userSearchLiveData, userSearch ->
            combine(availableWorkmatesLiveData.getValue(), workmatesGoingToRestaurantsLiveData.getValue(), userSearch)
        );
    }

    private void combine(
        @Nullable List<WorkmateEntity> availableWorkmates,
        @Nullable List<UserGoingToRestaurantEntity> workmatesGoingToRestaurant,
        @Nullable String userSearch
    ) {
        if (availableWorkmates == null || workmatesGoingToRestaurant == null) {
            return;
        }
        isUserSearchUnmatchedSingleLiveEvent.setValue(false);

        List<WorkmateItemViewState> workmateItemViewStateList = map(availableWorkmates, workmatesGoingToRestaurant, userSearch);
        workmatesViewStateMediatorLiveData.setValue(
            new WorkmatesViewState(workmateItemViewStateList, workmateItemViewStateList.isEmpty())
        );
    }

    public LiveData<WorkmatesViewState> getWorkmatesViewStateMediatorLiveData() {
        return workmatesViewStateMediatorLiveData;
    }

    @NonNull
    private List<WorkmateItemViewState> map(
        @NonNull List<WorkmateEntity> availableWorkmates,
        @NonNull List<UserGoingToRestaurantEntity> workmateGoingToRestaurantList,
        @Nullable String userSearch
    ) {
        List<WorkmateItemViewState> workmatesWithChoiceList = new ArrayList<>();
        List<WorkmateItemViewState> workmatesWithoutChoiceList = new ArrayList<>();
        List<WorkmateItemViewState> searchedWorkmatesWithChoiceList = new ArrayList<>();
        List<WorkmateItemViewState> searchedWorkmatesWithoutChoiceList = new ArrayList<>();
        List<WorkmateItemViewState> workmatesSortedList = new ArrayList<>();
        boolean isUserSearchMatched = false;

        for (WorkmateEntity workmateEntity : availableWorkmates) {
            String chosenRestaurantId = null;
            String chosenRestaurantName = null;
            WorkmateItemViewState workmateItemViewState;

            for (UserGoingToRestaurantEntity userGoingToRestaurantEntity : workmateGoingToRestaurantList) {
                if (userGoingToRestaurantEntity.getId().equals(workmateEntity.getId())) {
                    chosenRestaurantId = userGoingToRestaurantEntity.getChosenRestaurantId();
                    chosenRestaurantName = userGoingToRestaurantEntity.getChosenRestaurantName();
                }
            }

            if (userSearch != null) {
                if (chosenRestaurantName != null && chosenRestaurantName.contains(userSearch)) {
                    workmateItemViewState =
                        getWorkmateItemViewState(workmateEntity, chosenRestaurantId, chosenRestaurantName, true);

                    sortWorkmates(workmateItemViewState, searchedWorkmatesWithoutChoiceList, searchedWorkmatesWithChoiceList);

                    if (!isUserSearchMatched) {
                        isUserSearchMatched = true;
                    }
                } else {
                    workmateItemViewState =
                        getWorkmateItemViewState(workmateEntity, chosenRestaurantId, chosenRestaurantName, false);

                    sortWorkmates(workmateItemViewState, workmatesWithoutChoiceList, workmatesWithChoiceList);
                }

            } else {
                workmateItemViewState =
                    getWorkmateItemViewState(workmateEntity, chosenRestaurantId, chosenRestaurantName, false);

                sortWorkmates(workmateItemViewState, workmatesWithoutChoiceList, workmatesWithChoiceList);
            }

            // For sorting, workmates going to restaurants will be displayed first
            //sortWorkmates(workmateItemViewState, workmatesWithoutChoiceList, workmatesList);
        }

        if (userSearch != null && !isUserSearchMatched) {
            isUserSearchUnmatchedSingleLiveEvent.setValue(true);
        }

        workmatesSortedList.addAll(searchedWorkmatesWithChoiceList);
        workmatesSortedList.addAll(searchedWorkmatesWithoutChoiceList);
        workmatesSortedList.addAll(workmatesWithChoiceList);
        workmatesSortedList.addAll(workmatesWithoutChoiceList);

        return workmatesSortedList;
    }

    private void sortWorkmates(
        @NonNull WorkmateItemViewState workmateItemViewState,
        List<WorkmateItemViewState> workmatesWithoutChoiceList,
        List<WorkmateItemViewState> workmatesList
    ) {
        if (workmateItemViewState.getChosenRestaurantId() == null) {
            workmatesWithoutChoiceList.add(workmateItemViewState);
        } else {
            workmatesList.add(workmateItemViewState);
        }
    }

    @NonNull
    private WorkmateItemViewState getWorkmateItemViewState(
        @NonNull WorkmateEntity workmateEntity,
        String chosenRestaurantId,
        String chosenRestaurantName,
        boolean isSearched
    ) {
        return new WorkmateItemViewState(
            workmateEntity.getId(),
            workmateEntity.getPhotoUrl(),
            workmateEntity.getUsername(),
            chosenRestaurantId,
            chosenRestaurantName,
            isSearched
        );
    }

    public SingleLiveEvent<Boolean> getIsUserSearchUnmatchedSingleLiveEvent() {
        return isUserSearchUnmatchedSingleLiveEvent;
    }
}
