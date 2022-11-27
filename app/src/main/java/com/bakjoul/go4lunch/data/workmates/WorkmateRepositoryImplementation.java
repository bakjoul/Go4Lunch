package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bakjoul.go4lunch.data.user.UserGoingToRestaurantResponse;
import com.bakjoul.go4lunch.data.utils.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreQueryLiveData;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantsLiveData() {
        return new FirestoreCollectionLiveData<UserGoingToRestaurantResponse, UserGoingToRestaurantEntity>(
            firestoreDb.collection("chosenRestaurants"),
            UserGoingToRestaurantResponse.class
        ) {
            @Override
            public UserGoingToRestaurantEntity map(UserGoingToRestaurantResponse response) {
                final UserGoingToRestaurantEntity entity;

                if (response.getId() != null
                    && response.getUsername() != null
                    && response.getEmail() != null
                    && response.getPhotoUrl() != null
                    && response.getChosenRestaurantId() != null
                    && response.getChosenRestaurantName() != null
                    && firebaseAuth.getCurrentUser() != null
                    && !response.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                    entity = new UserGoingToRestaurantEntity(
                        response.getId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getPhotoUrl(),
                        response.getChosenRestaurantId(),
                        response.getChosenRestaurantName()
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
        return Transformations.switchMap(
            getWorkmatesGoingToRestaurantsLiveData(),
            response -> {
                Set<String> ids = new HashSet<>();
                if (response != null) {
                    for (UserGoingToRestaurantEntity entity : response) {
                        if (!entity.getId().equals(firebaseAuth.getUid())) {
                            ids.add(entity.getChosenRestaurantId());
                        }
                    }
                }
                return new MutableLiveData<>(ids);
            }
        );
    }

    @Override
    public LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantIdLiveData(String restaurantId) {
        return new FirestoreQueryLiveData<UserGoingToRestaurantResponse, UserGoingToRestaurantEntity>(
            firestoreDb.collection("chosenRestaurants").whereEqualTo("chosenRestaurantId", restaurantId),
            UserGoingToRestaurantResponse.class
        ) {
            @Override
            public UserGoingToRestaurantEntity map(UserGoingToRestaurantResponse response) {
                final UserGoingToRestaurantEntity entity;

                if (response.getId() != null
                    && response.getUsername() != null
                    && response.getEmail() != null
                    && response.getPhotoUrl() != null
                    && response.getChosenRestaurantId() != null
                    && response.getChosenRestaurantName() != null
                    && firebaseAuth.getCurrentUser() != null
                    && !response.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                    entity = new UserGoingToRestaurantEntity(
                        response.getId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getPhotoUrl(),
                        response.getChosenRestaurantId(),
                        response.getChosenRestaurantName()
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
        return Transformations.switchMap(
            getWorkmatesGoingToRestaurantsLiveData(),
            response -> {
                MutableLiveData<Map<String, Integer>> restaurantsAttendanceLiveData = new MutableLiveData<>();
                Map<String, Integer> restaurantsAttendance = new HashMap<>();
                if (response != null) {
                    for (UserGoingToRestaurantEntity entity : response) {
                        if (!entity.getId().equals(firebaseAuth.getUid())) {
                            restaurantsAttendance.merge(entity.getChosenRestaurantId(), 1, Integer::sum);
                        }
                    }
                }
                restaurantsAttendanceLiveData.setValue(restaurantsAttendance);
                return restaurantsAttendanceLiveData;
            }
        );
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
}
