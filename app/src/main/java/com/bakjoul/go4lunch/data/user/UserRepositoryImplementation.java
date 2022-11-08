package com.bakjoul.go4lunch.data.user;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreCollectionIdsLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreStringIdLiveData;
import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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

    @Override
    public LiveData<String> getChosenRestaurantLiveData() {
        if (firebaseAuth.getCurrentUser() != null) {
            return new FirestoreStringIdLiveData(
                firestoreDb.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                    .collection("chosenRestaurant").document("value")
            );
        }
        return null;
    }

/*    @Override
    public LiveData<WorkmateResponse> getCurrentUser() {
        final Uri photoUrl = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getPhotoUrl() : null;
        final WorkmateResponse currentUser = new WorkmateResponse(
            firebaseAuth.getCurrentUser().getUid(),
            firebaseAuth.getCurrentUser().getDisplayName(),
            firebaseAuth.getCurrentUser().getEmail(),
            photoUrl != null ? photoUrl.toString() : null
        );

        return new MutableLiveData<>(currentUser);
    }*/

    @Override
    public void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName) {
        final Map<String, Object> chosenRestaurantData = new HashMap<>();
        chosenRestaurantData.put(restaurantId, restaurantName);

        // Gets any previously chosen restaurant id
        final String[] previousChosenRestaurantId = new String[1];
        if (firebaseAuth.getCurrentUser() != null) {
            firestoreDb.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid()).collection("chosenRestaurant").document("value")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Map<String, Object> previouslyChosenRestaurantData = task.getResult().getData();
                        if (previouslyChosenRestaurantData != null) {
                            previousChosenRestaurantId[0] = previouslyChosenRestaurantData.keySet().toArray()[0].toString();
                        }

                        // Removes current user from previously chosen restaurant users
                        firestoreDb.collection("restaurants").document(previousChosenRestaurantId[0])
                            .collection("users")
                            .get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    for (DocumentSnapshot documentSnapshot : task2.getResult().getDocuments()) {
                                        if (documentSnapshot.getId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                            firestoreDb.collection("restaurants")
                                                .document(previousChosenRestaurantId[0]).collection("users")
                                                .document(firebaseAuth.getCurrentUser().getUid())
                                                .delete()
                                                .addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful()) {
                                                        Log.d(TAG, "User " + firebaseAuth.getCurrentUser().getDisplayName() + " was removed from restaurant " + previousChosenRestaurantId[0] + " users");

                                                        // Removes restaurant document from chosen restaurants if no users
                                                        removeRestaurantFromWorkmatesChosenRestaurants(previousChosenRestaurantId[0]);
                                                    }
                                                });
                                        }
                                    }
                                }
                            });
                    }
                });
        }

        // Updates current user chosen restaurant
        if (firebaseAuth.getCurrentUser() != null) {
            firestoreDb.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("chosenRestaurant")
                .document("value")
                .set(chosenRestaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant updated: " + restaurantId))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }

        // Creates restaurant document
        firestoreDb.collection("restaurants").document(restaurantId).set(chosenRestaurantData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final Map<String, Object> userData = new HashMap<>();
                userData.put("username", firebaseAuth.getCurrentUser().getDisplayName());
                // Adds current user to newly chosen restaurant users
                firestoreDb.collection("restaurants").document(restaurantId)
                    .collection("users")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .set(userData)
                    .addOnCompleteListener(documentReference -> Log.d(TAG, "User " + firebaseAuth.getCurrentUser().getDisplayName() + " added to restaurant " + restaurantId + " users"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
            }
        });
    }

    @Override
    public void unchooseRestaurant(@NonNull String restaurantId) {
        // Removes current user's chosen restaurant
        if (firebaseAuth.getCurrentUser() != null) {
            firestoreDb.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("chosenRestaurant")
                .document("value")
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, "Chosen restaurant deleted"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }

        // Removes current user from restaurant users
        firestoreDb.collection("restaurants").document(restaurantId).collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .delete()
            .addOnCompleteListener(documentReference -> {
                Log.d(TAG, "User " + firebaseAuth.getCurrentUser().getDisplayName() + " removed from restaurant " + restaurantId + " users");

                // Removes restaurant document from chosen restaurants if no users
                removeRestaurantFromWorkmatesChosenRestaurants(restaurantId);
            })
            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    }

    @Override
    public LiveData<Collection<String>> getFavoritesRestaurantsLiveData() {
        if (firebaseAuth.getCurrentUser() != null) {
            return new FirestoreCollectionIdsLiveData(
                firestoreDb.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                    .collection("favoriteRestaurants")
            );
        }
        return null;
    }

    private void removeRestaurantFromWorkmatesChosenRestaurants(@NonNull String restaurantId) {
        firestoreDb.collection("restaurants").document(restaurantId).collection("users")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().size() == 0) {
                    firestoreDb.collection("restaurants").document(restaurantId)
                        .delete()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Log.d(TAG, "Empty restaurant " + restaurantId + " removed from chosen restaurants");
                            }
                        });
                }
            });
    }


    @Override
    public void addRestaurantToFavorites(@NonNull String restaurantId, @NonNull String restaurantName) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("restaurantName", restaurantName);

        if (firebaseAuth.getCurrentUser() != null) {
            firestoreDb.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .set(restaurantData)
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " added to favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void removeRestaurantFromFavorites(@NonNull String restaurantId) {
        if (firebaseAuth.getCurrentUser() != null) {
            firestoreDb.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("favoriteRestaurants")
                .document(restaurantId)
                .delete()
                .addOnCompleteListener(documentReference -> Log.d(TAG, restaurantId + " removed from favorites"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
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
}
