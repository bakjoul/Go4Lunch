package com.bakjoul.go4lunch.data.workmates;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkmateRepositoryImplementation implements WorkmateRepository {

    private static final String TAG = "WorkmateRepository";

    @NonNull
    private final FirebaseFirestore firestoreDb;

    @NonNull
    private final FirebaseAuth firebaseAuth;

    private WorkmateResponse currentUser;

    private UserDataResponse currentUserData;

    @Inject
    public WorkmateRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreDb = firestoreDb;
        this.firebaseAuth = firebaseAuth;
    }

    public void setCurrentUser() {
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

        // Search for existing user
        firestoreDb.collection("users")
            .whereEqualTo("id", currentUser.getId())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        if (snapshot.getId().equals(currentUser.getId())) {
                            Log.d(TAG, "User already exists");
                        }
                    }

                    // If user already exists, retrieve their data
                    final Map<String, Object>[] chosenRestaurant = new Map[]{new HashMap<>()};
                    List<String> favoriteRestaurants = new ArrayList<>();

                    // Requests chosen restaurant data
                    if (currentUser.getId() != null) {
                        firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant").document("value")
                            .get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    chosenRestaurant[0] = task2.getResult().getData();
                                }

                                // Requests favorite restaurants data
                                firestoreDb.collection("users").document(currentUser.getId()).collection("favoriteRestaurants")
                                    .get()
                                    .addOnCompleteListener(task3 -> {
                                        if (task3.isSuccessful()) {
                                            for (DocumentSnapshot documentSnapshot : task3.getResult().getDocuments()) {
                                                favoriteRestaurants.add(documentSnapshot.getId());
                                            }
                                        }

                                        // Updates current user data
                                        currentUserData = new UserDataResponse(chosenRestaurant[0], favoriteRestaurants);
                                    });
                            });
                    }

                }
                // If user does not exists, push their basic data
                if (task.getResult().size() == 0) {
                    if (currentUser.getId() != null) {
                        firestoreDb.collection("users")
                            .document(currentUser.getId())
                            .set(currentUser)
                            .addOnSuccessListener(documentReference -> Log.d(TAG, "User successfully added"))
                            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
                    }
                }
            });
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

    public UserDataResponse getCurrentUserData() {
        return currentUserData;
    }

    public void setChosenRestaurant(@NonNull String restaurantId, @NonNull String restaurantName) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put(restaurantId, restaurantName);
        if (currentUser.getId() != null) {
            if (!restaurantId.isEmpty() && !restaurantName.isEmpty()) {
                firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant")
                    .document("value")
                    .set(restaurantData)
                    .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant successfully updated: " + restaurantId))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
            } else {
                firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant")
                    .document("value")
                    .delete()
                    .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant successfully deleted"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
            }
        }
    }

    public void addLikeRestaurant(String restaurantId, String restaurantName) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put(restaurantId, restaurantName);
        if (currentUser.getId() != null) {
            firestoreDb.collection("users").document(currentUser.getId()).collection("favoriteRestaurants")
                .document(restaurantId)
                .set(restaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully added to favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    public void removeLikedRestaurant(String restaurantId) {
        if (currentUser.getId() != null) {
            firestoreDb.collection("users").document(currentUser.getId()).collection("favoriteRestaurants")
                .document(restaurantId)
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " successfully removed from favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public LiveData<List<WorkmateEntity>> getAllWorkmatesForRestaurantId(String restaurantId) {
        MutableLiveData<List<WorkmateEntity>> result = new MutableLiveData<>();

        firestoreDb.collection("users")
            .whereEqualTo("restaurantId", restaurantId)
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
