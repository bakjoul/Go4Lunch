package com.bakjoul.go4lunch.data.workmates;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.FirestoreLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
                   }
                }
             }
             // If not, adds the user
             if (task.getResult().size() == 0) {
                Log.d(TAG, "Users does not exist");

                firestoreDb.collection("users")
                    .add(currentUser)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "User successfully added"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
             }
          });
   }

   public LiveData<List<Workmate>> getWorkmatesLiveData() {
      return new FirestoreLiveData<>(firestoreDb.collection("users"), Workmate.class);
   }

   public Workmate getCurrentUser() {
      return currentUser;
   }
}
