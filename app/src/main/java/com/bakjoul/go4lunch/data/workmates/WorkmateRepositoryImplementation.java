package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreChosenRestaurantAttendanceLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreChosenRestaurantsLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreWorkmatesWithChoiceLiveData;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

    @Override
    public LiveData<Collection<String>> getWorkmatesChosenRestaurantsLiveData() {
        if (firebaseAuth.getCurrentUser() != null) {
            return new FirestoreChosenRestaurantsLiveData(
                firestoreDb.collection("restaurants"),
                firestoreDb,
                firebaseAuth.getCurrentUser().getUid()
            );
        }
        return new MutableLiveData<>(new HashSet<>());
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

    @Override
    public LiveData<Map<String, Integer>> getRestaurantsAttendance() {
        if (firebaseAuth.getCurrentUser() != null) {
            return new FirestoreChosenRestaurantAttendanceLiveData(
                firestoreDb.collection("restaurants"),
                firestoreDb,
                firebaseAuth.getCurrentUser().getUid()
            );
        }
        return new MutableLiveData<>(new HashMap<>());
    }

    @Override
    public LiveData<Map<String, Map<String, Object>>> getWorkmatesWithChoiceLiveData() {
        return new FirestoreWorkmatesWithChoiceLiveData(
            firestoreDb.collection("usersWithChoice"),
            firestoreDb
        );
    }

}
