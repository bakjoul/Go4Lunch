package com.bakjoul.go4lunch.data.utils;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public abstract class FirestoreChatQueryLiveData<Response, Entity> extends LiveData<List<Entity>> {

    private static final String TAG = "FirestoreChatQueryLiveD";

    private final Query query;
    private final Class<Response> clazz;
    private final EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Log.i(TAG, "Listen failed", error);
                return;
            }

            if (querySnapshot != null) {
                List<Entity> entities = new ArrayList<>(querySnapshot.size());

                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    Response response = documentSnapshot.toObject(clazz);
                    if (response != null) {
                        Entity entity = map(response, documentSnapshot.getId());
                        if (entity != null) {
                            entities.add(entity);
                        }
                    }
                }

                setValue(entities);
            }
        }
    };

    private ListenerRegistration registration;

    public FirestoreChatQueryLiveData(Query query, Class<Response> clazz) {
        this.query = query;
        this.clazz = clazz;
    }

    public abstract Entity map(Response response, String documentId);

    @Override
    protected void onActive() {
        registration = query.addSnapshotListener(eventListener);
    }

    @Override
    protected void onInactive() {
        registration.remove();
        registration = null;
    }
}
