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
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;
import com.bakjoul.go4lunch.domain.location.GpsLocationRepository;
import com.bakjoul.go4lunch.domain.location.LocationPermissionRepository;
import com.bakjoul.go4lunch.domain.user.UserRepository;
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
    private final GpsLocationRepository gpsLocationRepository;

    @NonNull
    private final LocationPermissionRepository locationPermissionRepository;

    @NonNull
    private final AutocompleteRepository autocompleteRepository;

    private final MutableLiveData<BottomNavigationViewButton> bottomNavigationViewButtonMutableLiveData = new MutableLiveData<>(
        BottomNavigationViewButton.MAP
    );

    private final SingleLiveEvent<FragmentToDisplay> fragmentToDisplaySingleLiveEvent = new SingleLiveEvent<>();

    private LiveData<MainViewState> mainActivityViewStateLiveData;

    @Inject
    public MainViewModel(
        @ApplicationContext @NonNull Context context,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull GpsLocationRepository gpsLocationRepository,
        @NonNull LocationPermissionRepository locationPermissionRepository,
        @NonNull AutocompleteRepository autocompleteRepository,
        @NonNull UserRepository userRepository
    ) {
        this.context = context;
        this.firebaseAuth = firebaseAuth;
        this.gpsLocationRepository = gpsLocationRepository;
        this.locationPermissionRepository = locationPermissionRepository;
        this.autocompleteRepository = autocompleteRepository;

        if (firebaseAuth.getCurrentUser() != null) {
            userRepository.createFirestoreUser();

            mainActivityViewStateLiveData = Transformations.switchMap(
                userRepository.getChosenRestaurantLiveData(),
                response -> {
                    MainViewState mainViewState = new MainViewState(
                        firebaseAuth.getCurrentUser().getPhotoUrl(),
                        firebaseAuth.getCurrentUser().getDisplayName(),
                        firebaseAuth.getCurrentUser().getEmail(),
                        null
                    );

                    // Updates view state if user has chosen a restaurant
                    if (response != null) {
                        mainViewState = new MainViewState(
                            firebaseAuth.getCurrentUser().getPhotoUrl(),
                            firebaseAuth.getCurrentUser().getDisplayName(),
                            firebaseAuth.getCurrentUser().getEmail(),
                            response.getChosenRestaurantId()
                        );
                    }
                    return new MutableLiveData<>(mainViewState);
                }
            );
        }

        LiveData<Boolean> isLocationPermissionEnabledLiveData = locationPermissionRepository.getLocationPermissionLiveData();

        fragmentToDisplaySingleLiveEvent.addSource(bottomNavigationViewButtonMutableLiveData, bottomNavigationViewButton ->
            combine(bottomNavigationViewButton, isLocationPermissionEnabledLiveData.getValue()));

        fragmentToDisplaySingleLiveEvent.addSource(isLocationPermissionEnabledLiveData, isLocationPermissionEnabled ->
            combine(bottomNavigationViewButtonMutableLiveData.getValue(), isLocationPermissionEnabled));
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
                fragmentToDisplaySingleLiveEvent.setValue(FragmentToDisplay.RESTAURANTS);
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
            gpsLocationRepository.startLocationUpdates();
            locationPermissionRepository.setLocationPermission(true);
        } else {
            gpsLocationRepository.stopLocationUpdates();
            locationPermissionRepository.setLocationPermission(false);
        }
    }

    public void onBottomNavigationClicked(BottomNavigationViewButton button) {
        bottomNavigationViewButtonMutableLiveData.setValue(button);
    }

    public void onSearchViewQueryTextChanged(String userInput) {
        autocompleteRepository.setUserQuery(userInput);
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
        RESTAURANTS(R.id.main_bottomNavigationView_menuItem_listView),
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
