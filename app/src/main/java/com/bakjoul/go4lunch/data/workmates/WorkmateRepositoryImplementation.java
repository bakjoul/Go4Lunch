package com.bakjoul.go4lunch.data.workmates;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkmateRepositoryImplementation implements WorkmateRepository {

    private static final String TAG = "WorkmateRepositoryImple";

    @NonNull
    private final FirebaseFirestore firestoreDb;

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    public WorkmateRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreDb = firestoreDb;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public LiveData<List<WorkmateEntity>> getAvailableWorkmatesLiveData() {
        return new FirestoreCollectionLiveData<WorkmateResponse, WorkmateEntity>(
            firestoreDb.collection("users"),
            WorkmateResponse.class
        ) {
            @Override
            public WorkmateEntity map(WorkmateResponse response) {
                final WorkmateEntity entity;

                if (response.getId() != null
                    && response.getUsername() != null
                    && response.getEmail() != null) {
                    entity = new WorkmateEntity(
                        response.getId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getPhotoUrl()
                    );
                } else {
                    entity = null;
                }

                return entity;
            }
        };
    }

    @Override
    public LiveData<List<String>> getChosenRestaurantsLiveData() {
        List<String> test = new ArrayList<>();
        firestoreDb.collection("restaurants").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed", error);
                return;
            }

            if (querySnapshot != null) {
                for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                    if (test.contains(documentChange.getDocument().getId())) {
                        test.remove(documentChange.getDocument().getId());
                    } else {
                        test.add(documentChange.getDocument().getId());
                    }

                }
            }
            Log.d("test", "onEvent: " + test);
        });
        return new MutableLiveData<>(test);
    }

    @Override
    public LiveData<List<WorkmateEntity>> getWorkmatesForRestaurantIdLiveData(String restaurantId) {
        return new FirestoreCollectionLiveData<WorkmateResponse, WorkmateEntity>(
            firestoreDb.collection("restaurants").document(restaurantId).collection("users"),
            WorkmateResponse.class
        ) {
            @Override
            public WorkmateEntity map(WorkmateResponse response) {
                final WorkmateEntity entity;

                if (response.getId() != null
                    && response.getUsername() != null
                    && response.getEmail() != null
                    && firebaseAuth.getCurrentUser() != null
                    && !response.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                    entity = new WorkmateEntity(
                        response.getId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getPhotoUrl()
                    );
                } else {
                    entity = null;
                }

                return entity;
            }
        };
    }

}
