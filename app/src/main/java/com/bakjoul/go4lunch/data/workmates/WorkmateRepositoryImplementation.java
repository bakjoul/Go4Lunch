package com.bakjoul.go4lunch.data.workmates;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkmateRepositoryImplementation implements WorkmateRepository {

    private static final String TAG = "WorkmateRepository";

    @NonNull
    private final FirebaseFirestore firestoreDb;

    private WorkmateResponse currentUser;

    @Inject
    public WorkmateRepositoryImplementation(@NonNull FirebaseFirestore firestoreDb) {
        this.firestoreDb = firestoreDb;
    }

    public void setCurrentUser(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            final Uri photoUrl = firebaseUser.getPhotoUrl();
            currentUser = new WorkmateResponse(
                firebaseUser.getUid(),
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                photoUrl != null ? photoUrl.toString() : null
            );
        }

        // Checks if user already in database
        firestoreDb.collection("users")
            .document(currentUser.getId())
            .set(currentUser);
    }

    public LiveData<List<WorkmateEntity>> getWorkmatesLiveData() {
        return new FirestoreCollectionLiveData<WorkmateResponse, WorkmateEntity>(
            firestoreDb.collection("users"),
            WorkmateResponse.class
        ) {
            @Override
            public WorkmateEntity map(WorkmateResponse workmateResponse) {
                final WorkmateEntity entity;

                if (workmateResponse.getId() != null
                    && workmateResponse.getUsername() != null
                    && workmateResponse.getEmail() != null) {
                    entity = new WorkmateEntity(
                        workmateResponse.getId(),
                        workmateResponse.getUsername(),
                        workmateResponse.getEmail(),
                        workmateResponse.getPhotoUrl()
                    );
                } else {
                    entity = null;
                }

                return entity;
            }
        };
    }

    public WorkmateResponse getCurrentUser() {
        return currentUser;
    }

    public void setChosenRestaurant(@NonNull String restaurantId, @Nullable String restaurantName) {
        firestoreDb.collection("users").document(currentUser.getId())
            .update("chosenRestaurant.restaurantId", restaurantId, "chosenRestaurant.restaurantName", restaurantName)
            .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant successfully updated: " + restaurantId))
            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        updateCurrentUserData();
    }

    public void addLikeRestaurant(String restaurantId, String restaurantName) {
        firestoreDb.collection("users").document(currentUser.getId())
            .update("likedRestaurants", FieldValue.arrayUnion(Collections.singletonMap(restaurantId, restaurantName)))
            .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully added to liked restaurants"))
            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        updateCurrentUserData();
    }

    public void removeLikedRestaurant(String restaurantId, String restaurantName) {
        firestoreDb.collection("users").document(currentUser.getId())
            .update("likedRestaurants", FieldValue.arrayRemove(Collections.singletonMap(restaurantId, restaurantName)))
            .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully removed from liked restaurants"))
            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        updateCurrentUserData();
    }

    @Override
    public LiveData<List<WorkmateEntity>> getAllWorkmatesForPlaceId(String placeId) {
        MutableLiveData<List<WorkmateEntity>> result = new MutableLiveData<>();

        firestoreDb.collection("users")
            .whereEqualTo("placeId", placeId)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    error.printStackTrace();
                } else if (value != null) {
                    List<WorkmateResponse> responses = value.toObjects(WorkmateResponse.class);
                    List<WorkmateEntity> entities = new ArrayList<>(responses.size());

                    for (WorkmateResponse response : responses) {
                        if (response.getId() != null
                            && response.getUsername() != null
                            && response.getEmail() != null) {
                            entities.add(
                                new WorkmateEntity(
                                    response.getId(),
                                    response.getUsername(),
                                    response.getEmail(),
                                    response.getPhotoUrl()
                                )
                            );
                        }
                    }

                    result.setValue(entities);
                }
            });

        return result;
    }
}
