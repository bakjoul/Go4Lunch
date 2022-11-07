package com.bakjoul.go4lunch.data;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FirestoreCollectionIdsLiveData extends LiveData<Collection<String>> {

    private static final String TAG = "FirestoreColIdsLiveData";

    private final CollectionReference collectionReference;
    private final EventListener<QuerySnapshot> eventListener = (querySnapshot, error) -> {
        if (error != null) {
            Log.i(TAG, "Listen failed", error);
            return;
        }

        if (querySnapshot != null) {
            Set<String> ids = new HashSet<>(querySnapshot.size());
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                ids.add(document.getId());
            }
            Log.d("test", "onEvent: " + ids); // TODO DELETE ?

            setValue(ids);
        }
    };

    private ListenerRegistration registration;

    public FirestoreCollectionIdsLiveData(CollectionReference collectionReference) {
        this.collectionReference = collectionReference;
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
