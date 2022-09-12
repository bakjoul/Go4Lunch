package com.bakjoul.go4lunch.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.LocationRepository;
import com.bakjoul.go4lunch.data.PermissionRepository;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    @SuppressLint("StaticFieldLeak")
    @NonNull
    private final Context context;

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @NonNull
    private final LocationRepository locationRepository;

    @NonNull
    private final PermissionRepository permissionRepository;

    private final MutableLiveData<MainViewState> mainActivityViewStateLiveData = new MutableLiveData<>();

    @Inject
    public MainViewModel(@ApplicationContext @NonNull Context context, @NonNull FirebaseAuth firebaseAuth, @NonNull LocationRepository locationRepository, @NonNull PermissionRepository permissionRepository) {
        this.context = context;
        this.firebaseAuth = firebaseAuth;
        this.locationRepository = locationRepository;
        this.permissionRepository = permissionRepository;

        if (firebaseAuth.getCurrentUser() != null) {
            mainActivityViewStateLiveData.setValue(
                new MainViewState(
                    firebaseAuth.getCurrentUser().getPhotoUrl(),
                    firebaseAuth.getCurrentUser().getDisplayName(),
                    firebaseAuth.getCurrentUser().getEmail()
                )
            );
        }
    }

    public LiveData<MainViewState> getMainActivityViewStateLiveData() {
        return mainActivityViewStateLiveData;
    }

    public void logOut() {
        firebaseAuth.signOut();

        // Handles Facebook log out
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedInOnFacebook = accessToken != null && !accessToken.isExpired();
        if (isLoggedInOnFacebook) {
            LoginManager.getInstance().logOut();
            Log.d(TAG, "logOut: logged out of facebook");
        }
    }

    public void onResume() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationRepository.startLocationUpdates();
        } else {
            locationRepository.stopLocationUpdates();
        }
    }

    public void onLocationPermissionGranted() {
        permissionRepository.onLocationPermissionGranted();
    }

    public void onLocationPermissionDenied() {
        permissionRepository.onLocationPermissionDenied();
    }
}
