package com.bakjoul.go4lunch.data.workmates;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
                currentUserData = new WorkmateData(new WorkmateChosenRestaurant("", null), null);

                firestoreDb.collection("users").document(currentUser.getId())
                    .set(currentUser)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "User successfully added"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));

                firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant")
                    .document("chosenRestaurantDoc")
                    .set(new WorkmateChosenRestaurant(null, null))
                    .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant document successfully set"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));

                firestoreDb.collection("users").document(currentUser.getId()).collection("likedRestaurants")
                    .document("emptyDoc")
                    .set(new WorkmateChosenRestaurant(null, null))
                    .addOnCompleteListener(documentReference -> Log.d(TAG, "Liked restaurant document successfully set"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
             }
          });
   }

   private void updateCurrentUserData() {
      List<WorkmateChosenRestaurant> workmateChosenRestaurant = new ArrayList<>();
      firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant")
          .get()
          .addOnCompleteListener(task -> {
             if (task.isSuccessful() && task.getResult().size() > 0) {
                workmateChosenRestaurant.add(task.getResult().getDocuments().get(0).toObject(WorkmateChosenRestaurant.class));
             }
          });

      List<WorkmateLikedRestaurant> workmateLikedRestaurantList = new ArrayList<>();
      firestoreDb.collection("users").document(currentUser.getId()).collection("likedRestaurants")
          .get()
          .addOnCompleteListener(task -> {
             if (task.isSuccessful() && task.getResult().size() > 1) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                   if (!documentSnapshot.getId().equals("emptyDoc")) {
                      WorkmateLikedRestaurant likedRestaurant = documentSnapshot.toObject(WorkmateLikedRestaurant.class);
                      workmateLikedRestaurantList.add(likedRestaurant);
                   }
                }
             }
             currentUserData = new WorkmateData(workmateChosenRestaurant.get(0), workmateLikedRestaurantList);
             Log.d(TAG, "User data updated");
          });
   }

   public LiveData<List<Workmate>> getWorkmatesLiveData() {
      return new FirestoreLiveData<>(firestoreDb.collection("users"), Workmate.class);
   }

   public WorkmateData getCurrentUserData() {
      return currentUserData;
   }

   public Workmate getCurrentUser() {
      return currentUser;
   }

   @RequiresApi(api = Build.VERSION_CODES.N)
   public void setChosenRestaurant(@NonNull String restaurantId, @Nullable String restaurantName) {
      firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant")
          .document("chosenRestaurantDoc")
          .update("restaurantId", restaurantId, "restaurantName", restaurantName)
          .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant successfully updated: " + restaurantId))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
      updateCurrentUserData();
   }

   public void removeLikedRestaurant(String restaurantId) {
      firestoreDb.collection("users").document(currentUser.getId()).collection("likedRestaurants")
          .document(restaurantId)
          .delete()
          .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully removed from liked restaurants"))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
      updateCurrentUserData();
   }

   public void addLikeRestaurant(String restaurantId, String restaurantName) {
      firestoreDb.collection("users").document(currentUser.getId()).collection("likedRestaurants")
          .document(restaurantId)
          .set(new WorkmateLikedRestaurant(restaurantId, restaurantName))
          .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully added to liked restaurants"))
          .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
      updateCurrentUserData();
   }
}
