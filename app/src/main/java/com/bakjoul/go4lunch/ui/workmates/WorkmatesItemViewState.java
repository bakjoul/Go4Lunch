package com.bakjoul.go4lunch.ui.workmates;

import androidx.annotation.NonNull;

import java.util.Objects;

public class WorkmatesItemViewState {

   @NonNull
   private final String id;

   @NonNull
   private final String photoUrl;

   @NonNull
   private final String name;

   public WorkmatesItemViewState(@NonNull String id, @NonNull String photoUrl, @NonNull String name) {
      this.id = id;
      this.photoUrl = photoUrl;
      this.name = name;
   }

   @NonNull
   public String getId() {
      return id;
   }

   @NonNull
   public String getPhotoUrl() {
      return photoUrl;
   }

   @NonNull
   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      WorkmatesItemViewState that = (WorkmatesItemViewState) o;
      return id.equals(that.id) && photoUrl.equals(that.photoUrl) && name.equals(that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, photoUrl, name);
   }

   @NonNull
   @Override
   public String toString() {
      return "WorkmatesItemViewState{" +
          "id='" + id + '\'' +
          ", photoUrl='" + photoUrl + '\'' +
          ", name='" + name + '\'' +
          '}';
   }
}
