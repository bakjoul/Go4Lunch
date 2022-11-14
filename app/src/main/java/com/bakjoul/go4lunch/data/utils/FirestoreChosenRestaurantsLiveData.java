package com.bakjoul.go4lunch.data.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FirestoreChosenRestaurantsLiveData extends LiveData<Collection<String>> {

    private static final String TAG = "FirestoreChosenRestsLiv";

    private final CollectionReference collectionReference;
    private FirebaseFirestore firestoreDb;
    private final String userId;
    private final EventListener<QuerySnapshot> eventListener = (querySnapshot, error) -> {
        if (error != null) {
            Log.i(TAG, "Listen failed", error);
            return;
        }

        if (querySnapshot != null) {
            Set<String> ids = new HashSet<>(querySnapshot.size());
            setValue(ids);

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                firestoreDb.collection("restaurants").document(document.getId()).collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    if (task.getResult().size() > 1 || (task.getResult().size() == 1 && !documentSnapshot.getId().equals(userId))) {
                                        ids.add(document.getId());
                                    }
                                }
                            }
                            setValue(ids);
                        }
                    });
            }
        }
    };

    private ListenerRegistration registration;

    public FirestoreChosenRestaurantsLiveData(
        CollectionReference collectionReference,
        FirebaseFirestore firestoreDb,
        String userId) {
        this.collectionReference = collectionReference;
        this.firestoreDb = firestoreDb;
        this.userId = userId;
    }

    @Override
    protected void onActive() {
        registration = collectionReference.addSnapshotListener(eventListener);
    }

    @Override
    protected void onInactive() {
        registration.remove();
        registration = null;
    }
}
