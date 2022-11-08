package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

    @NonNull
    private final WorkmateRepositoryImplementation workmateRepositoryImplementation;

    @NonNull
    private final FirebaseAuth firebaseAuth;

    private final LiveData<WorkmatesViewState> workmatesViewStateMutableLiveData;

    @Inject
    public WorkmatesViewModel(
        @NonNull WorkmateRepositoryImplementation workmateRepositoryImplementation,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.workmateRepositoryImplementation = workmateRepositoryImplementation;
        this.firebaseAuth = firebaseAuth;

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
                for (WorkmateEntity workmateEntity : workmateList) {
                    if (firebaseAuth.getCurrentUser() != null
                        && !workmateEntity.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                        WorkmatesItemViewState workmatesItemViewState = new WorkmatesItemViewState(
                            workmateEntity.getId(),
                            workmateEntity.getPhotoUrl(),
                            workmateEntity.getUsername()
                        );
                        workmatesItemViewStateList.add(workmatesItemViewState);
                    }
                }
                return workmatesItemViewStateList;
            }
        );
    }
}
