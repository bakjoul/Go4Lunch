package com.bakjoul.go4lunch.data.workmates;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.FirestoreLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkmateRepository {

   private static final String TAG = "WorkmateRepository";

   @NonNull
   private final FirebaseFirestore firestoreDb;

   private Workmate currentUser;

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
             photoUrl != null ? photoUrl.toString() : null,
             new WorkmateChosenRestaurant("", ""),
             new ArrayList<>()
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
                      updateCurrentUserData();
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
             }
          });
   }

   private void updateCurrentUserData() {
      firestoreDb.collection("users").document(currentUser.getId())
          .get()
          .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             Workmate result;

             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                   result = task.getResult().toObject(Workmate.class);
                   if (result != null) {
                      currentUser.setChosenRestaurant(result.getChosenRestaurant());
                      currentUser.setLikedRestaurants(result.getLikedRestaurants());
                   }
                }
             }
          });
   }

   public LiveData<List<Workmate>> getWorkmatesLiveData() {
      return new FirestoreLiveData<>(firestoreDb.collection("users"), Workmate.class);
   }

   public Workmate getCurrentUser() {
      return currentUser;
   }

   @RequiresApi(api = Build.VERSION_CODES.N)
   public void setChosenRestaurant(@NonNull String restaurantId, @Nullable String restaurantName) {
      firestoreDb.collection("users").document(currentUser.getId())
          .update("chosenRestaurant.restaurantId", restaurantId, "chosenRestaurant.restaurantName", restaurantName)
          .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant successfully updated: " + restaurantId))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
      updateCurrentUserData();
   }

   public void addLikeRestaurant(String restaurantId, String restaurantName) {
      firestoreDb.collection("users").document(currentUser.getId())
          .update("likedRestaurants", FieldValue.arrayUnion(Collections.singletonMap(restaurantId, restaurantName)))
          .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully added to liked restaurants"))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
      updateCurrentUserData();
   }

   public void removeLikedRestaurant(String restaurantId, String restaurantName) {
      firestoreDb.collection("users").document(currentUser.getId())
          .update("likedRestaurants", FieldValue.arrayRemove(Collections.singletonMap(restaurantId, restaurantName)))
          .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully removed from liked restaurants"))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
      updateCurrentUserData();
   }
}
