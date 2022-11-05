package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.user.UserRepositoryImplementation;
import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

    @NonNull
    private final UserRepositoryImplementation userRepositoryImplementation;

    @NonNull
    private final WorkmateRepositoryImplementation workmateRepositoryImplementation;

    private final LiveData<WorkmatesViewState> workmatesViewStateMutableLiveData;

    @Inject
    public WorkmatesViewModel(
        @NonNull UserRepositoryImplementation userRepositoryImplementation,
        @NonNull WorkmateRepositoryImplementation workmateRepositoryImplementation
    ) {
        this.userRepositoryImplementation = userRepositoryImplementation;
        this.workmateRepositoryImplementation = workmateRepositoryImplementation;

        workmatesViewStateMutableLiveData = Transformations.switchMap(
            getWorkmatesLiveData(),
            itemViewStateList -> new MutableLiveData<>(new WorkmatesViewState(itemViewStateList, itemViewStateList.isEmpty()))
        );
    }

    public LiveData<WorkmatesViewState> getWorkmatesViewStateMutableLiveData() {
        return workmatesViewStateMutableLiveData;
    }

    @NonNull
    private LiveData<List<WorkmatesItemViewState>> getWorkmatesLiveData() {
        return Transformations.map(
            workmateRepositoryImplementation.getAvailableWorkmatesLiveData(), workmateList -> {
                List<WorkmatesItemViewState> workmatesItemViewStateList = new ArrayList<>();
                for (WorkmateEntity workmateResponse : workmateList) {
                    if (!workmateResponse.getId().equals(userRepositoryImplementation.getCurrentUser().getId())) {
                        WorkmatesItemViewState workmatesItemViewState = new WorkmatesItemViewState(
                            workmateResponse.getId(),
                            workmateResponse.getPhotoUrl(),
                            workmateResponse.getUsername()
                        );
                        workmatesItemViewStateList.add(workmatesItemViewState);
                    }
                }
                return workmatesItemViewStateList;
            }
        );
    }
}
