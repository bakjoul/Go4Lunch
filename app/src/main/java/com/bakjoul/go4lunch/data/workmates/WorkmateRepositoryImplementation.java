package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreCollectionIdsLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkmateRepositoryImplementation implements WorkmateRepository {

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
    public LiveData<Collection<String>> getAllChosenRestaurantsLiveData() {
        return new FirestoreCollectionIdsLiveData(firestoreDb.collection("restaurants"));
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
