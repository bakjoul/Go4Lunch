package com.bakjoul.go4lunch.data;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

@SuppressWarnings("unchecked")
public abstract class FirestoreDocumentLiveData<Response, Entity> extends LiveData<Entity> {

    private static final String TAG = "FirestoreDocumentLiveData";

    private final DocumentReference documentReference;
    private final Class<Response> clazz;
    private final EventListener<DocumentSnapshot> eventListener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Log.i(TAG, "Listen failed", error);
                return;
            }

            if (documentSnapshot != null) {
                Response response = documentSnapshot.toObject(clazz);
                Entity entity = map(response);

                if (entity != null) {
                    setValue(entity);
                }
            }
        }
    };

    private ListenerRegistration registration;

    public FirestoreDocumentLiveData(DocumentReference documentReference, Class<Response> clazz) {
        this.documentReference = documentReference;
        this.clazz = clazz;
    }

    public abstract Entity map(Response response);

    @Override
    protected void onActive() {
        super.onActive();
        registration = documentReference.addSnapshotListener(eventListener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (!hasActiveObservers()) {
            registration.remove();
            registration = null;
        }
    }
}
