package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class WorkmatesViewState {

   private final List<WorkmatesItemViewState> workmatesItemViewStateList;
   private final boolean isEmptyStateVisible;

   public WorkmatesViewState(List<WorkmatesItemViewState> workmatesItemViewStateList, boolean isEmptyStateVisible) {
      this.workmatesItemViewStateList = workmatesItemViewStateList;
      this.isEmptyStateVisible = isEmptyStateVisible;
   }

   public List<WorkmatesItemViewState> getWorkmatesItemViewStateList() {
      return workmatesItemViewStateList;
   }

   public boolean isEmptyStateVisible() {
      return isEmptyStateVisible;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      WorkmatesViewState that = (WorkmatesViewState) o;
      return isEmptyStateVisible == that.isEmptyStateVisible && Objects.equals(workmatesItemViewStateList, that.workmatesItemViewStateList);
   }

   @Override
   public int hashCode() {
      return Objects.hash(workmatesItemViewStateList, isEmptyStateVisible);
   }

   @NonNull
   @Override
   public String toString() {
      return "WorkmatesViewState{" +
          "workmatesItemViewStates=" + workmatesItemViewStateList +
          ", isEmptyStateVisible=" + isEmptyStateVisible +
          '}';
   }
}
