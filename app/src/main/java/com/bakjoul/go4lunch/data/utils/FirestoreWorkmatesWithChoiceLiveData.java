package com.bakjoul.go4lunch.data.utils;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreWorkmatesWithChoiceLiveData extends LiveData<Map<String, Map<String, Object>>> {

    private static final String TAG = "FirestoreWorkmatesWithC";

    private final CollectionReference collectionReference;
    private final FirebaseFirestore firestoreDb;

    private final EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Log.i(TAG, "Listen failed", error);
                return;
            }

            if (querySnapshot != null) {
                Map<String, Map<String, Object>> result = new HashMap<>();
                setValue(result);

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    firestoreDb.collection("users").document(document.getId()).collection("chosenRestaurant")
                        .document("value")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                result.put(document.getId(), task.getResult().getData());
                            }
                            setValue(result);
                        });
                }
            }
        }
    };

    private ListenerRegistration registration;

    public FirestoreWorkmatesWithChoiceLiveData(CollectionReference collectionReference, FirebaseFirestore firestoreDb) {
        this.collectionReference = collectionReference;
        this.firestoreDb = firestoreDb;
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
