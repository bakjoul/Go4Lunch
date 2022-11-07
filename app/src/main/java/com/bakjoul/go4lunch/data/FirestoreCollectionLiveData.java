package com.bakjoul.go4lunch.data;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public abstract class FirestoreCollectionLiveData<Response, Entity> extends LiveData<List<Entity>> {

    private static final String TAG = "FirestoreCollecLiveData";

    private final CollectionReference collectionReference;
    private final Class<Response> clazz;
    private final EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Log.i(TAG, "Listen failed", error);
                return;
            }

            if (querySnapshot != null) {
                List<Response> responses = querySnapshot.toObjects(clazz);
                List<Entity> entities = new ArrayList<>(responses.size());

                for (Response response : responses) {
                    Entity entity = map(response);

                    if (entity != null) {
                        entities.add(entity);
                    }
                }

                setValue(entities);
            }
        }
    };

    private ListenerRegistration registration;

    public FirestoreCollectionLiveData(CollectionReference collectionReference, Class<Response> clazz) {
        this.collectionReference = collectionReference;
        this.clazz = clazz;
    }

    public abstract Entity map(Response response);

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
