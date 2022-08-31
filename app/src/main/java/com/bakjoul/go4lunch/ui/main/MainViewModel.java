package com.bakjoul.go4lunch.ui.main;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.FirebaseRepository;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.UserInfo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    @NonNull
    private final FirebaseRepository firebaseRepository;

    private final MutableLiveData<MainViewState> mainActivityViewStateLiveData = new MutableLiveData<>();

    @Nullable
    private Uri photoUrl;
    @Nullable
    private String username;
    @Nullable
    private String email;

    @Inject
    public MainViewModel(@NonNull FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;

        if (firebaseRepository.getCurrentUser() != null) {
            photoUrl = firebaseRepository.getCurrentUser().getPhotoUrl();
            username = firebaseRepository.getCurrentUser().getDisplayName();
            email = firebaseRepository.getCurrentUser().getEmail();

            mainActivityViewStateLiveData.setValue(
                new MainViewState(photoUrl, username, email)
            );
        }
    }

    public LiveData<MainViewState> getMainActivityViewStateLiveData() {
        return mainActivityViewStateLiveData;
    }

    public void getCurrentUser() {
        firebaseRepository.getCurrentUser();
    }

    public void logOut() {
        for (UserInfo userInfo : firebaseRepository.getCurrentUser().getProviderData())

            Log.d("test", firebaseRepository.getCurrentUser().getProviderData().toString());
        //if (firebaseRepository.getCurrentUser().getProviderId().equals("facebook.com")) {

        LoginManager.getInstance().logOut();
        Log.d("test", "logOut: facebook");
        //}
        firebaseRepository.signOut();
    }
}
