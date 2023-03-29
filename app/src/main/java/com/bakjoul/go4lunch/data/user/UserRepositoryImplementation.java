package com.bakjoul.go4lunch.data.user;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreCollectionIdsLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreDocumentLiveData;
import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;
import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.google.firebase.auth.FirebaseUser;
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
    private final GetCurrentFirebaseUserUseCase getCurrentFirebaseUserUseCase;

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @Inject
    public UserRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull GetCurrentFirebaseUserUseCase getCurrentFirebaseUserUseCase,
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.firestoreDb = firestoreDb;
        this.getCurrentFirebaseUserUseCase = getCurrentFirebaseUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Override
    public void createFirestoreUser() {
        FirebaseUser firebaseUser = getCurrentFirebaseUserUseCase.invoke();

        if (firebaseUser != null) {
            firestoreDb.collection("users")
                .document(firebaseUser.getUid())
                .set(map(firebaseUser));
        }
    }

    @NonNull
    private WorkmateResponse map(@NonNull FirebaseUser firebaseUser) {
        final Uri photoUrl = firebaseUser.getPhotoUrl();

        return new WorkmateResponse(
            firebaseUser.getUid(),
            firebaseUser.getDisplayName(),
            firebaseUser.getEmail(),
            photoUrl != null ? photoUrl.toString() : null
        );
    }

    @Override
    public void chooseRestaurant(
        @NonNull String restaurantId,
        @NonNull String restaurantName,
        @NonNull String restaurantAddress
    ) {
        String currentUserId = getCurrentUserUseCase.invoke();
        if (currentUserId != null) {
            final Map<String, Object> chosenRestaurantData = getCurrentUserData();
            chosenRestaurantData.put("chosenRestaurantId", restaurantId);
            chosenRestaurantData.put("chosenRestaurantName", restaurantName);
            chosenRestaurantData.put("chosenRestaurantAddress", restaurantAddress);

            firestoreDb
                .collection("usersGoingToRestaurants")
                .document(currentUserId)
                .set(chosenRestaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant updated (" + restaurantId + ") for user " + currentUserId))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @NonNull
    private Map<String, Object> getCurrentUserData() {
        final Map<String, Object> userData = new HashMap<>();
        FirebaseUser currentUser = getCurrentFirebaseUserUseCase.invoke();
        if (currentUser != null) {
            userData.put("id", currentUser.getUid());
            userData.put("username", currentUser.getDisplayName());
            userData.put("email", currentUser.getEmail());
            userData.put("photoUrl", currentUser.getPhotoUrl());
        }

        return userData;
    }

    @Override
    public void unchooseRestaurant() {
        String currentUserId = getCurrentUserUseCase.invoke();
        if (currentUserId != null) {
            firestoreDb
                .collection("usersGoingToRestaurants")
                .document(currentUserId)
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant deleted for user " + currentUserId))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName) {
        String currentUserId = getCurrentUserUseCase.invoke();
        if (currentUserId != null) {
            final Map<String, Object> restaurantData = new HashMap<>();
            restaurantData.put("restaurantId", restaurantId);
            restaurantData.put("restaurantName", restaurantName);

            firestoreDb.collection("users")
                .document(currentUserId)
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .set(restaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " added to favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void removeRestaurantFromFavorites(@NonNull String restaurantId) {
        String currentUserId = getCurrentUserUseCase.invoke();
        if (currentUserId != null) {
            firestoreDb.collection("users")
                .document(currentUserId)
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " removed from favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public LiveData<UserGoingToRestaurantEntity> getChosenRestaurantLiveData() {
        String currentUserId = getCurrentUserUseCase.invoke();
        if (currentUserId != null) {
            return new FirestoreDocumentLiveData<UserGoingToRestaurantResponse, UserGoingToRestaurantEntity>(
                firestoreDb.collection("usersGoingToRestaurants").document(currentUserId),
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
    public LiveData<Collection<String>> getFavoritesRestaurantsLiveData() {
        String currentUserId = getCurrentUserUseCase.invoke();
        if (currentUserId != null) {
            return new FirestoreCollectionIdsLiveData(
                firestoreDb
                    .collection("users")
                    .document(currentUserId)
                    .collection("favoriteRestaurants")
            );
        }

        return new MutableLiveData<>();
    }
}
