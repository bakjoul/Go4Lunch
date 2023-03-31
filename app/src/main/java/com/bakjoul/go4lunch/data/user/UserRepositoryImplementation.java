package com.bakjoul.go4lunch.data.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreCollectionIdsLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreDocumentLiveData;
import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;
import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepositoryImplementation implements UserRepository {

    private static final String TAG = "UserRepositoryImplement";

    @NonNull
    private final FirebaseFirestore firestoreDb;

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @Inject
    public UserRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.firestoreDb = firestoreDb;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Override
    public void createFirestoreUser(@Nullable LoggedUserEntity currentUser) {
        if (currentUser != null) {
            firestoreDb.collection("users")
                .document(currentUser.getId())
                .set(map(currentUser));
        } else {
            Log.d(TAG, "Could not create Firestore user because current user is null");
        }
    }

    @NonNull
    private WorkmateResponse map(@NonNull LoggedUserEntity currentUser) {
        return new WorkmateResponse(
            currentUser.getId(),
            currentUser.getUsername(),
            currentUser.getEmail(),
            currentUser.getPhotoUrl()
        );
    }

    @Override
    public void chooseRestaurant(
        @Nullable LoggedUserEntity currentUser,
        @NonNull String restaurantId,
        @NonNull String restaurantName,
        @NonNull String restaurantAddress
    ) {
        if (currentUser != null) {
            final Map<String, Object> chosenRestaurantData = getCurrentUserData();
            chosenRestaurantData.put("chosenRestaurantId", restaurantId);
            chosenRestaurantData.put("chosenRestaurantName", restaurantName);
            chosenRestaurantData.put("chosenRestaurantAddress", restaurantAddress);

            firestoreDb
                .collection("usersGoingToRestaurants")
                .document(currentUser.getId())
                .set(chosenRestaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant updated (" + restaurantId + ") for user " + currentUser.getId()))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @NonNull
    private Map<String, Object> getCurrentUserData() {
        final Map<String, Object> userData = new HashMap<>();
        final LoggedUserEntity currentUser = getCurrentUserUseCase.invoke();
        if (currentUser != null) {
            userData.put("id", currentUser.getId());
            userData.put("username", currentUser.getUsername());
            userData.put("email", currentUser.getEmail());
            userData.put("photoUrl", currentUser.getPhotoUrl());
        }

        return userData;
    }

    @Override
    public void unchooseRestaurant(@Nullable LoggedUserEntity currentUser) {
        if (currentUser != null) {
            firestoreDb
                .collection("usersGoingToRestaurants")
                .document(currentUser.getId())
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant deleted for user " + currentUser.getId()))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void addRestaurantToFavorites(
        @Nullable LoggedUserEntity currentUser,
        @NonNull String restaurantId,
        @NonNull String restaurantName
    ) {
        if (currentUser != null) {
            final Map<String, Object> restaurantData = new HashMap<>();
            restaurantData.put("restaurantId", restaurantId);
            restaurantData.put("restaurantName", restaurantName);

            firestoreDb.collection("users")
                .document(currentUser.getId())
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .set(restaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " added to favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void removeRestaurantFromFavorites(
        @Nullable LoggedUserEntity currentUser,
        @NonNull String restaurantId
    ) {
        if (currentUser != null) {
            firestoreDb.collection("users")
                .document(currentUser.getId())
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " removed from favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public LiveData<UserGoingToRestaurantEntity> getChosenRestaurantLiveData(@Nullable LoggedUserEntity currentUser) {
        if (currentUser != null) {
            return new FirestoreDocumentLiveData<UserGoingToRestaurantResponse, UserGoingToRestaurantEntity>(
                firestoreDb.collection("usersGoingToRestaurants").document(currentUser.getId()),
                UserGoingToRestaurantResponse.class
            ) {
                @Override
                public UserGoingToRestaurantEntity map(UserGoingToRestaurantResponse response) {
                    final UserGoingToRestaurantEntity entity;

                    if (response != null
                        && response.getId() != null
                        && response.getUsername() != null
                        && response.getEmail() != null
                        && response.getPhotoUrl() != null
                        && response.getChosenRestaurantId() != null
                        && response.getChosenRestaurantName() != null
                        && response.getChosenRestaurantAddress() != null) {
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

        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Collection<String>> getFavoritesRestaurantsLiveData(@Nullable LoggedUserEntity currentUser) {
        if (currentUser != null) {
            return new FirestoreCollectionIdsLiveData(
                firestoreDb
                    .collection("users")
                    .document(currentUser.getId())
                    .collection("favoriteRestaurants")
            );
        }

        return new MutableLiveData<>();
    }
}
