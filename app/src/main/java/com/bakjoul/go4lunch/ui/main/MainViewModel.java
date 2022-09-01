package com.bakjoul.go4lunch.ui.main;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    @NonNull
    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<MainViewState> mainActivityViewStateLiveData = new MutableLiveData<>();

    @Inject
    public MainViewModel(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;

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
}
