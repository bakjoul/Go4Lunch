package com.bakjoul.go4lunch.data.workmates;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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

   @Inject
   public WorkmateRepository(@NonNull FirebaseFirestore firestoreDb, @NonNull FirebaseAuth firebaseAuth) {
      this.firestoreDb = firestoreDb;

      setCurrentUser(firebaseAuth);
   }

   private void setCurrentUser(@NonNull FirebaseAuth firebaseAuth) {
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
      MutableLiveData<List<Workmate>> workmatesListLiveData = new MutableLiveData<>();
      firestoreDb.collection("users")
          .addSnapshotListener(new EventListener<QuerySnapshot>() {
             @Override
             public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                   Log.w(TAG, "Listen failed", error);
                }

                if (snapshot != null && snapshot.size() > 0) {
                   List<Workmate> workmateList = new ArrayList<>();
                   for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                      Workmate workmate = documentSnapshot.toObject(Workmate.class);
                      workmateList.add(workmate);
                   }
                   workmatesListLiveData.setValue(workmateList);
                }
             }
          });
      return workmatesListLiveData;
   }

}
