package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.workmates.Workmate;
import com.bakjoul.go4lunch.data.workmates.WorkmateRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkmatesViewModel extends ViewModel {

   @NonNull
   private final WorkmateRepository workmateRepository;

   private final LiveData<WorkmatesViewState> workmatesViewStateMutableLiveData;

   @Inject
   public WorkmatesViewModel(@NonNull WorkmateRepository workmateRepository) {
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
             for (Workmate workmate : workmateList) {
                WorkmatesItemViewState workmatesItemViewState = new WorkmatesItemViewState(
                    workmate.getId(),
                    workmate.getPhotoUrl(),
                    workmate.getUsername()
                );
                workmatesItemViewStateList.add(workmatesItemViewState);
             }
             return workmatesItemViewStateList;
          }
      );
   }
}
