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
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;
import com.bakjoul.go4lunch.domain.autocomplete.GetAutocompletePredictionsUseCase;
import com.bakjoul.go4lunch.domain.dispatcher.IsUserAuthenticatedUseCase;
import com.bakjoul.go4lunch.domain.location.GpsLocationRepository;
import com.bakjoul.go4lunch.domain.location.LocationPermissionRepository;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final MutableLiveData<String> currentUserSearchMutableLiveData = new MutableLiveData<>(null);

    private final SingleLiveEvent<FragmentToDisplay> fragmentToDisplaySingleLiveEvent = new SingleLiveEvent<>();

    private final MediatorLiveData<MainViewState> mainViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public MainViewModel(
        @ApplicationContext @NonNull Context context,
        @NonNull FirebaseAuth firebaseAuth,
        @NonNull IsUserAuthenticatedUseCase isUserAuthenticatedUseCase,
        @NonNull GetAutocompletePredictionsUseCase getAutocompletePredictionsUseCase,
        @NonNull GpsLocationRepository gpsLocationRepository,
        @NonNull LocationPermissionRepository locationPermissionRepository,
        @NonNull AutocompleteRepository autocompleteRepository,
        @NonNull UserRepository userRepository) {
        this.context = context;
        this.firebaseAuth = firebaseAuth;
        this.gpsLocationRepository = gpsLocationRepository;
        this.locationPermissionRepository = locationPermissionRepository;
        this.autocompleteRepository = autocompleteRepository;

        if (isUserAuthenticatedUseCase.invoke()) {
            userRepository.createFirestoreUser();

            LiveData<UserGoingToRestaurantEntity> userChosenRestaurantLiveData = userRepository.getChosenRestaurantLiveData();

            LiveData<List<PredictionResponse>> predictionsLiveData = Transformations.switchMap(currentUserSearchMutableLiveData, userSearch ->
                getAutocompletePredictionsUseCase.invoke(userSearch)
            );

            mainViewStateMediatorLiveData.addSource(userChosenRestaurantLiveData, userChosenRestaurant ->
                combine(userChosenRestaurant, predictionsLiveData.getValue())
            );
            mainViewStateMediatorLiveData.addSource(predictionsLiveData, predictions ->
                combine(userChosenRestaurantLiveData.getValue(), predictions)
            );
        } else {
            mainViewStateMediatorLiveData.setValue(
                new MainViewState(
                    null,
                    "User not logged",
                    "",
                    null,
                    new ArrayList<>()
                )
            );
        }

        // Sets up fragmentToDisplaySingleLiveEvent
        LiveData<Boolean> isLocationPermissionEnabledLiveData = locationPermissionRepository.getLocationPermissionLiveData();

        fragmentToDisplaySingleLiveEvent.addSource(bottomNavigationViewButtonMutableLiveData, bottomNavigationViewButton ->
            combineForFragmentToDisplay(bottomNavigationViewButton, isLocationPermissionEnabledLiveData.getValue())
        );
        fragmentToDisplaySingleLiveEvent.addSource(isLocationPermissionEnabledLiveData, isLocationPermissionEnabled ->
            combineForFragmentToDisplay(bottomNavigationViewButtonMutableLiveData.getValue(), isLocationPermissionEnabled)
        );
    }

    private void combine(
        @Nullable UserGoingToRestaurantEntity userChosenRestaurant,
        @Nullable List<PredictionResponse> predictionsList
    ) {
        Set<String> suggestionsSet = new HashSet<>();
        if (predictionsList != null) {
            for (PredictionResponse predictionResponse : predictionsList) {
                suggestionsSet.add(predictionResponse.getStructuredFormatting().getMainText());
            }
        }

        mainViewStateMediatorLiveData.setValue(
            new MainViewState(
                firebaseAuth.getCurrentUser().getPhotoUrl(),
                firebaseAuth.getCurrentUser().getDisplayName(),
                firebaseAuth.getCurrentUser().getEmail(),
                userChosenRestaurant != null ? userChosenRestaurant.getChosenRestaurantId() : null,
                new ArrayList<>(suggestionsSet)
            )
        );
    }

    private void combineForFragmentToDisplay(
        @Nullable BottomNavigationViewButton bottomNavigationViewButton,
        @Nullable Boolean isLocationPermissionEnabled
    ) {
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

    public LiveData<MainViewState> getMainViewStateMediatorLiveData() {
        return mainViewStateMediatorLiveData;
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

    public void onSearchViewQueryTextChanged(String input) {
        currentUserSearchMutableLiveData.setValue(input);
    }

    public void onSuggestionClicked(String suggestion) {
        autocompleteRepository.setUserSearch(suggestion);
        currentUserSearchMutableLiveData.setValue(null);
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
