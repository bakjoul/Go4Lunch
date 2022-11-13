package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class WorkmatesViewState {

   private final List<WorkmateItemViewState> workmateItemViewStateList;
   private final boolean isEmptyStateVisible;

   public WorkmatesViewState(List<WorkmateItemViewState> workmateItemViewStateList, boolean isEmptyStateVisible) {
      this.workmateItemViewStateList = workmateItemViewStateList;
      this.isEmptyStateVisible = isEmptyStateVisible;
   }

   public List<WorkmateItemViewState> getWorkmatesItemViewStateList() {
      return workmateItemViewStateList;
   }

   public boolean isEmptyStateVisible() {
      return isEmptyStateVisible;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      WorkmatesViewState that = (WorkmatesViewState) o;
      return isEmptyStateVisible == that.isEmptyStateVisible && Objects.equals(workmateItemViewStateList, that.workmateItemViewStateList);
   }

   @Override
   public int hashCode() {
      return Objects.hash(workmateItemViewStateList, isEmptyStateVisible);
   }

   @NonNull
   @Override
   public String toString() {
      return "WorkmatesViewState{" +
          "workmatesItemViewStates=" + workmateItemViewStateList +
          ", isEmptyStateVisible=" + isEmptyStateVisible +
          '}';
   }
}
