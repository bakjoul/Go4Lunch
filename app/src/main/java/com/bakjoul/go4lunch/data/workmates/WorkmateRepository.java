package com.bakjoul.go4lunch.data.workmates;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.FirestoreLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkmateRepository {

   private static final String TAG = "WorkmateRepository";

   @NonNull
   private final FirebaseFirestore firestoreDb;

   private Workmate currentUser;
   private WorkmateData currentUserData;

   @Inject
   public WorkmateRepository(@NonNull FirebaseFirestore firestoreDb) {
      this.firestoreDb = firestoreDb;
   }

   public void setCurrentUser(@NonNull FirebaseAuth firebaseAuth) {
      FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
      if (firebaseUser != null) {
         final Uri photoUrl = firebaseUser.getPhotoUrl();
         currentUser = new Workmate(
             firebaseUser.getUid(),
             firebaseUser.getDisplayName(),
             firebaseUser.getEmail(),
             photoUrl != null ? photoUrl.toString() : null
         );
         currentUserData = new WorkmateData(
             null,
             null
         );
      }

      // Checks if user already in database
      firestoreDb.collection("users")
          .whereEqualTo("id", currentUser.getId())
          .get().addOnCompleteListener(task -> {
             if (task.isSuccessful()) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                   if ((currentUser.getUsername()).equals(snapshot.getString("username"))) {
                      Log.d(TAG, "User already exists");
                      retrieveCurrentUserData();
                   }
                }
             }
             // If not, adds the user
             if (task.getResult().size() == 0) {
                Log.d(TAG, "Users does not exist");

                firestoreDb.collection("users").document(currentUser.getId())
                    .set(currentUser)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "User successfully added"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));

                firestoreDb.collection("users")
                    .document(currentUser.getId()).collection("data")
                    .document(currentUser.getId() + "data")
                    .set(currentUserData)
                    .addOnCompleteListener(documentReference -> Log.d(TAG, "User data successfully added"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
             }
          });
   }

   private void retrieveCurrentUserData() {
      firestoreDb.collection("users")
          .document(currentUser.getId()).collection("data")
          .document(currentUser.getId() + "data")
          .get().addOnCompleteListener(task -> {
             if (task.isSuccessful()) {
                currentUserData = task.getResult().toObject(WorkmateData.class);
             }
          });
      Log.d(TAG, "User data retrieved");
   }

   public LiveData<List<Workmate>> getWorkmatesLiveData() {
      return new FirestoreLiveData<>(firestoreDb.collection("users"), Workmate.class);
   }

   public Workmate getCurrentUser() {
      return currentUser;
   }

   public WorkmateData getCurrentUserData() {
      return currentUserData;
   }

   public void setChosenRestaurant(@Nullable String restaurantId) {
      firestoreDb.collection("users")
          .document(currentUser.getId()).collection("data")
          .document(currentUser.getId() + "data")
          .update("chosenRestaurantId", restaurantId)
          .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant successfully updated: " + restaurantId))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
   }

   public void removeLikedRestaurant(String restaurantId) {
      currentUserData.getLikedRestaurantsIds().remove(restaurantId);
      firestoreDb.collection("users")
          .document(currentUser.getId()).collection("data")
          .document(currentUser.getId() + "data")
          .update("likedRestaurantsIds", currentUserData.getLikedRestaurantsIds())
          .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully removed from liked restaurants"))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
   }

   public void addLikeRestaurant(String restaurantId) {
      if (currentUserData.getLikedRestaurantsIds() == null) {
         currentUserData.setLikedRestaurantsIds(new ArrayList<>());
      }
      currentUserData.getLikedRestaurantsIds().add(restaurantId);
      firestoreDb.collection("users")
          .document(currentUser.getId()).collection("data")
          .document(currentUser.getId() + "data")
          .update("likedRestaurantsIds", currentUserData.getLikedRestaurantsIds())
          .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully added to liked restaurants"))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
   }
}
