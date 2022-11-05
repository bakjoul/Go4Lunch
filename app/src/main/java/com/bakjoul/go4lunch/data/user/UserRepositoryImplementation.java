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

    private WorkmateResponse currentUser;

    private final MutableLiveData<Map<String, Object>> chosenRestaurantLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<String>> favoriteRestaurantsLiveData = new MutableLiveData<>();

    @Inject
    public UserRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreDb = firestoreDb;

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
    }

    public LiveData<Map<String, Object>> getChosenRestaurantLiveData() {
        return chosenRestaurantLiveData;
    }

    public LiveData<List<String>> getFavoriteRestaurantsLiveData() {
        return favoriteRestaurantsLiveData;
    }

    @Override
    public void initCurrentUser() {
        // Search for user on database
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
                    // Requests user's chosen restaurant data
                    if (currentUser.getId() != null) {
                        firestoreDb.collection("users").document(currentUser.getId()).collection("chosenRestaurant").document("value")
                            .get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    chosenRestaurantLiveData.setValue(task2.getResult().getData());
                                }

                                // Requests user's favorite restaurants data
                                List<String> favoriteRestaurants = new ArrayList<>();
                                firestoreDb.collection("users").document(currentUser.getId()).collection("favoriteRestaurants")
                                    .get()
                                    .addOnCompleteListener(task3 -> {
                                        if (task3.isSuccessful()) {
                                            for (DocumentSnapshot documentSnapshot : task3.getResult().getDocuments()) {
                                                favoriteRestaurants.add(documentSnapshot.getId());
                                            }
                                        }

                                        // Updates current user data
                                        favoriteRestaurantsLiveData.setValue(favoriteRestaurants);
                                    });
                            });
                    }

                }
                // If user does not exists, push their basic info to database
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

    @Override
    public WorkmateResponse getCurrentUser() {
        return currentUser;
    }

    @Override
    public void chooseRestaurant(@NonNull String restaurantId, @NonNull String restaurantName) {
        final Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put(restaurantId, restaurantName);

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

        // Adds current user to restaurant users
        firestoreDb.collection("restaurants").document(restaurantId).collection("users")
            .document(currentUser.getId())
            .set(currentUser)
            .addOnCompleteListener(documentReference -> Log.d(TAG, "User " + currentUser.getUsername() + " added to restaurant " + restaurantId + " users"))
            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
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
            .addOnCompleteListener(documentReference -> Log.d(TAG, "User " + currentUser.getUsername() + " removed from restaurant " + restaurantId + " users"))
            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
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

}
