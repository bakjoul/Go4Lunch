package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkmateRepositoryImplementation implements WorkmateRepository {

    @NonNull
    private final FirebaseFirestore firestoreDb;

    @Inject
    public WorkmateRepositoryImplementation(@NonNull FirebaseFirestore firestoreDb) {
        this.firestoreDb = firestoreDb;
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

}
