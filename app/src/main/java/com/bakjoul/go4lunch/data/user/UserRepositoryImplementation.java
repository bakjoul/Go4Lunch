package com.bakjoul.go4lunch.data.user;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.workmates.WorkmateResponse;
import com.bakjoul.go4lunch.domain.user.UserRepository;
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

    public LiveData<Map<String, String>> getChosenRestaurantLiveData() {
        return chosenRestaurantLiveData;
    }

    public LiveData<List<String>> getFavoriteRestaurantsLiveData() {
        return favoriteRestaurantsLiveData;
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
    public LiveData<WorkmateResponse> getCurrentUser() {
        return currentUser;
    }

    @Override
    public void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put(restaurantId, restaurantName);

        // Removes current user from any previously chosen restaurant users
        if (chosenRestaurantLiveData.getValue() != null) {
            String previousRestaurantId = chosenRestaurantLiveData.getValue().keySet().toArray()[0];
            firestoreDb.collection("restaurants")
                .document(previousRestaurantId)
                .collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            if (documentSnapshot.getId().equals(currentUser.getId())) {
                                firestoreDb.collection("restaurants").document(previousRestaurantId).collection("users")
                                    .document(currentUser.getId())
                                    .delete()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Log.d(TAG, "User " + currentUser.getUsername() + " was removed from restaurant " + previousRestaurantId + " users");

                                            removeRestaurantFromChosenRestaurants(previousRestaurantId);
                                        }
                                    });
                            }
                        }
                    }
                });
        }

        // Updates current user chosen restaurant
        if (currentUser.getId() != null) {
            firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant")
                .document("value")
                .set(restaurantData)
                .addOnCompleteListener(documentReference -> {
                    Log.d(TAG, "Chosen restaurant updated: " + restaurantId);
                    chosenRestaurantLiveData.setValue(restaurantData);
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }

        firestoreDb.collection("restaurants").document(restaurantId).set(restaurantData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Adds current user to newly chosen restaurant users
                firestoreDb.collection("restaurants").document(restaurantId)
                    .collection("users")
                    .document(currentUser.getId())
                    .set(currentUser)
                    .addOnCompleteListener(documentReference -> Log.d(TAG, "User " + currentUser.getUsername() + " added to restaurant " + restaurantId + " users"))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
            }
        });
    }

    @Override
    public void unchooseRestaurant(@NonNull String restaurantId) {
        // Removes current user's chosen restaurant
        if (currentUser.getId() != null) {
            firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant")
                .document("value")
                .delete()
                .addOnCompleteListener(documentReference -> {
                    Log.d(TAG, "Chosen restaurant deleted");
                    chosenRestaurantLiveData.setValue(null);
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }

        // Removes current user from restaurant users
        firestoreDb.collection("restaurants").document(restaurantId).collection("users")
            .document(currentUser.getId())
            .delete()
            .addOnCompleteListener(documentReference -> {
                Log.d(TAG, "User " + currentUser.getUsername() + " removed from restaurant " + restaurantId + " users");

                // Removes restaurant from chosen restaurants if no users
                removeRestaurantFromChosenRestaurants(restaurantId);
            })
            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    }

    private void removeRestaurantFromChosenRestaurants(@NonNull String restaurantId) {
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
        restaurantData.put(restaurantId, restaurantName);

        List<String> favoriteRestaurants = favoriteRestaurantsLiveData.getValue();
        if (favoriteRestaurants == null) {
            favoriteRestaurants = new ArrayList<>();
        }

        if (currentUser.getId() != null) {
            List<String> finalFavoriteRestaurants = favoriteRestaurants;
            firestoreDb.collection("users").document(currentUser.getId()).collection("favoriteRestaurants")
                .document(restaurantId)
                .set(restaurantData)
                .addOnCompleteListener(documentReference -> {
                    Log.d(TAG, restaurantId + " added to favorites");
                    finalFavoriteRestaurants.add(restaurantId);
                    favoriteRestaurantsLiveData.setValue(finalFavoriteRestaurants);
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

    @Override
    public void removeRestaurantFromFavorites(@NonNull String restaurantId) {
        List<String> favoriteRestaurants = favoriteRestaurantsLiveData.getValue();

        if (currentUser.getId() != null) {
            firestoreDb.collection("users").document(currentUser.getId()).collection("favoriteRestaurants")
                .document(restaurantId)
                .delete()
                .addOnCompleteListener(documentReference -> {
                    Log.d(TAG, restaurantId + " removed from favorites");
                    if (favoriteRestaurants != null) {
                        favoriteRestaurants.remove(restaurantId);
                        favoriteRestaurantsLiveData.setValue(favoriteRestaurants);
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
        }
    }

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
