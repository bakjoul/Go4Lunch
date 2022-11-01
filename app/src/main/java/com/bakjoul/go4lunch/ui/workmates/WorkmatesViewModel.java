package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;
import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

   @NonNull
   private final WorkmateRepositoryImplementation workmateRepository;

   private final LiveData<WorkmatesViewState> workmatesViewStateMutableLiveData;

   @Inject
   public WorkmatesViewModel(@NonNull WorkmateRepositoryImplementation workmateRepository) {
      this.workmateRepository = workmateRepository;

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
          workmateRepository.getWorkmatesLiveData(), workmateList -> {
             List<WorkmatesItemViewState> workmatesItemViewStateList = new ArrayList<>();
             for (WorkmateResponse workmateResponse : workmateList) {
                if (!workmateRepository.getCurrentUser().getId().equals(workmateResponse.getId())) {
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
