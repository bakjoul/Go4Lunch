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

import java.util.HashMap;
import java.util.Map;

public class FirestoreChosenRestaurantAttendanceLiveData extends LiveData<Map<String, Integer>> {

    private static final String TAG = "FirestoreAttendanceLive";

    private final CollectionReference collectionReference;
    private FirebaseFirestore firestoreDb;
    private final String userId;
    private final EventListener<QuerySnapshot> eventListener = (querySnapshot, error) -> {
        if (error != null) {
            Log.i(TAG, "Listen failed", error);
            return;
        }

        if (querySnapshot != null) {
            Map<String, Integer> restaurantsAttendance = new HashMap<>(querySnapshot.size());
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                firestoreDb.collection("restaurants").document(document.getId()).collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                int attendance = task.getResult().size();
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    if (documentSnapshot.getId().equals(userId)) {
                                        attendance -= 1;
                                    }
                                }
                                if (attendance > 0) {
                                    restaurantsAttendance.put(document.getId(), attendance);
                                }
                            }
                            setValue(restaurantsAttendance);
                        }
                    });
            }
            setValue(restaurantsAttendance);
        }
    };

    private ListenerRegistration registration;

    public FirestoreChosenRestaurantAttendanceLiveData(
        CollectionReference collectionReference,
        FirebaseFirestore firestoreDb,
        String userId
    ) {
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
