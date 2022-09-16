package com.bakjoul.go4lunch.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.repository.LocationRepository;
import com.bakjoul.go4lunch.data.repository.PermissionRepository;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;
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

    private final MutableLiveData<BottomNavigationViewButton> bottomNavigationViewButtonMutableLiveData = new MutableLiveData<>(
        BottomNavigationViewButton.MAP
    );

    private final SingleLiveEvent<FragmentToDisplay> fragmentToDisplaySingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public MainViewModel(
        @ApplicationContext @NonNull Context context,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull LocationRepository locationRepository,
        @NonNull PermissionRepository permissionRepository
    ) {
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

        LiveData<Boolean> isLocationPermissionEnabledLiveData = permissionRepository.getLocationPermissionLiveData();

        fragmentToDisplaySingleLiveEvent.addSource(bottomNavigationViewButtonMutableLiveData, bottomNavigationViewButton ->
            combine(bottomNavigationViewButton, isLocationPermissionEnabledLiveData.getValue()));

        fragmentToDisplaySingleLiveEvent.addSource(isLocationPermissionEnabledLiveData, isLocationPermissionEnabled -> {
            Log.d("Nino", "onChanged() called with: isLocationPermissionEnabled = [" + isLocationPermissionEnabled + "]");
            combine(bottomNavigationViewButtonMutableLiveData.getValue(), isLocationPermissionEnabled);
        });
    }

    private void combine(@Nullable BottomNavigationViewButton bottomNavigationViewButton, @Nullable Boolean isLocationPermissionEnabled) {
        if (bottomNavigationViewButton == null || isLocationPermissionEnabled == null) {
            return;
        }

        switch (bottomNavigationViewButton) {
            case MAP:
                if (isLocationPermissionEnabled) {
                    fragmentToDisplaySingleLiveEvent.setValue(FragmentToDisplay.MAP);
                } else {
                    fragmentToDisplaySingleLiveEvent.setValue(FragmentToDisplay.NO_PERMISSION);
                }
                break;
            case RESTAURANTS:
                fragmentToDisplaySingleLiveEvent.setValue(FragmentToDisplay.RESTAURANT);
                break;
            case WORKMATES:
                fragmentToDisplaySingleLiveEvent.setValue(FragmentToDisplay.WORKMATES);
                break;
        }
    }

    public LiveData<MainViewState> getMainActivityViewStateLiveData() {
        return mainActivityViewStateLiveData;
    }

    public SingleLiveEvent<FragmentToDisplay> getFragmentToDisplaySingleLiveEvent() {
        return fragmentToDisplaySingleLiveEvent;
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
            permissionRepository.setLocationPermission(true);
        } else {
            locationRepository.stopLocationUpdates();
            permissionRepository.setLocationPermission(false);
        }
    }

    public void onBottomNavigationClicked(BottomNavigationViewButton button) {
        bottomNavigationViewButtonMutableLiveData.setValue(button);
    }

    public enum BottomNavigationViewButton {
        MAP(R.id.main_bottomNavigationView_menuItem_mapView),
        RESTAURANTS(R.id.main_bottomNavigationView_menuItem_listView),
        WORKMATES(R.id.main_bottomNavigationView_menuItem_workmates);

        @IdRes
        private final int menuId;

        BottomNavigationViewButton(@IdRes int menuId) {
            this.menuId = menuId;
        }

        @NonNull
        public static BottomNavigationViewButton fromMenuId(@IdRes int menuId) {
            for (BottomNavigationViewButton value : BottomNavigationViewButton.values()) {
                if (value.menuId == menuId) {
                    return value;
                }
            }

            throw new IllegalStateException("Unknown menuId: " + menuId);
        }
    }

    public enum FragmentToDisplay {
        MAP(R.id.main_bottomNavigationView_menuItem_mapView),
        RESTAURANT(R.id.main_bottomNavigationView_menuItem_listView),
        WORKMATES(R.id.main_bottomNavigationView_menuItem_workmates),
        NO_PERMISSION(R.id.main_bottomNavigationView_menuItem_mapView);

        private final int menuItemId;

        FragmentToDisplay(int menuItemId) {
            this.menuItemId = menuItemId;
        }

        public int getMenuItemId() {
            return menuItemId;
        }
    }
}
