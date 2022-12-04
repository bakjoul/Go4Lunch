package com.bakjoul.go4lunch.data.user;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreCollectionIdsLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreDocumentLiveData;
import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
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
    private final FirebaseAuth firebaseAuth;

    @Inject
    public UserRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreDb = firestoreDb;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void createFirestoreUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

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
    public void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName) {
        final Map<String, Object> chosenRestaurantData = getCurrentUserData();
        chosenRestaurantData.put("chosenRestaurantId", restaurantId);
        chosenRestaurantData.put("chosenRestaurantName", restaurantName);

        if (firebaseAuth.getUid() != null) {
            firestoreDb
                .collection("usersGoingToRestaurants")
                .document(firebaseAuth.getUid())
                .set(chosenRestaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant updated (" + restaurantId + ") for user " + firebaseAuth.getUid()))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @NonNull
    private Map<String, Object> getCurrentUserData() {
        final Map<String, Object> userData = new HashMap<>();
        if (firebaseAuth.getCurrentUser() != null) {
            userData.put("id", firebaseAuth.getCurrentUser().getUid());
            userData.put("username", firebaseAuth.getCurrentUser().getDisplayName());
            userData.put("email", firebaseAuth.getCurrentUser().getEmail());
            userData.put("photoUrl", firebaseAuth.getCurrentUser().getPhotoUrl());
        }
        return userData;
    }

    @Override
    public void unchooseRestaurant() {
        if (firebaseAuth.getUid() != null) {
            firestoreDb
                .collection("usersGoingToRestaurants")
                .document(firebaseAuth.getUid())
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant deleted for user " + firebaseAuth.getUid()))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("restaurantId", restaurantId);
        restaurantData.put("restaurantName", restaurantName);

        if (firebaseAuth.getUid() != null) {
            firestoreDb.collection("users")
                .document(firebaseAuth.getUid())
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .set(restaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " added to favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void removeRestaurantFromFavorites(@NonNull String restaurantId) {
        if (firebaseAuth.getUid() != null) {
            firestoreDb.collection("users")
                .document(firebaseAuth.getUid())
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " removed from favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public LiveData<UserGoingToRestaurantEntity> getChosenRestaurantLiveData() {
        if (firebaseAuth.getUid() != null) {
            return new FirestoreDocumentLiveData<UserGoingToRestaurantResponse, UserGoingToRestaurantEntity>(
                firestoreDb.collection("usersGoingToRestaurants").document(firebaseAuth.getUid()),
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
                        && response.getChosenRestaurantName() != null) {
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
        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Collection<String>> getFavoritesRestaurantsLiveData() {
        if (firebaseAuth.getUid() != null) {
            return new FirestoreCollectionIdsLiveData(
                firestoreDb
                    .collection("users")
                    .document(firebaseAuth.getUid())
                    .collection("favoriteRestaurants")
            );
        }
        return new MutableLiveData<>();
    }
}
