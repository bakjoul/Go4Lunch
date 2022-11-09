package com.bakjoul.go4lunch.data.utils;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Map;

public class FirestoreSingleIdLiveData extends LiveData<String> {

    private static final String TAG = "FirestoreSingleIdLiveDa";

    private final DocumentReference documentReference;
    private final EventListener<DocumentSnapshot> eventListener = (documentSnapshot, error) -> {
        if (error != null) {
            Log.i(TAG, "Listen failed", error);
            return;
        }

        if (documentSnapshot != null) {
            Map<String, Object> data = documentSnapshot.getData();
            String id = "";
            if (data != null) {
                id = data.keySet().toArray()[0].toString();
            }

            setValue(id);
        }
    };

    private ListenerRegistration registration;

    public FirestoreSingleIdLiveData(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    @Override
    protected void onActive() {
        registration = documentReference.addSnapshotListener(eventListener);
    }

    @Override
    protected void onInactive() {
        registration.remove();
        registration = null;
    }
}
