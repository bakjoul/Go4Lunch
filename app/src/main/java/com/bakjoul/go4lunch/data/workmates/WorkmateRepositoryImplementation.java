package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.user.UserGoingToRestaurantResponse;
import com.bakjoul.go4lunch.data.utils.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreQueryLiveData;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
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
    public WorkmateRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb
    ) {
        this.firestoreDb = firestoreDb;
    }

    @Override
    public LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantsLiveData() {
        return new FirestoreCollectionLiveData<UserGoingToRestaurantResponse, UserGoingToRestaurantEntity>(
            firestoreDb.collection("usersGoingToRestaurants"),
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
                    && response.getChosenRestaurantAddress() != null
                ) {
                    entity = new UserGoingToRestaurantEntity(
                        response.getId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getPhotoUrl(),
                        response.getChosenRestaurantId(),
                        response.getChosenRestaurantName(),
                        response.getChosenRestaurantAddress()
                    );
                } else {
                    entity = null;
                }

                return entity;
            }
        };
    }

    @Override
    public LiveData<List<UserGoingToRestaurantEntity>> getWorkmatesGoingToRestaurantIdLiveData(String restaurantId) {
        return new FirestoreQueryLiveData<UserGoingToRestaurantResponse, UserGoingToRestaurantEntity>(
            firestoreDb.collection("usersGoingToRestaurants").whereEqualTo("chosenRestaurantId", restaurantId),
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
                    && response.getChosenRestaurantAddress() != null
                ) {
                    entity = new UserGoingToRestaurantEntity(
                        response.getId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getPhotoUrl(),
                        response.getChosenRestaurantId(),
                        response.getChosenRestaurantName(),
                        response.getChosenRestaurantAddress()
                    );
                } else {
                    entity = null;
                }

                return entity;
            }
        };
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
                ) {
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
