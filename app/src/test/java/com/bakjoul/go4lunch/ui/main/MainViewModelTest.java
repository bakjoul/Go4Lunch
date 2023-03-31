package com.bakjoul.go4lunch.ui.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.autocomplete.model.PredictionResponse;
import com.bakjoul.go4lunch.data.autocomplete.model.StructuredFormattingResponse;
import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LogOutUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;
import com.bakjoul.go4lunch.domain.autocomplete.GetAutocompletePredictionsUseCase;
import com.bakjoul.go4lunch.domain.dispatcher.IsUserAuthenticatedUseCase;
import com.bakjoul.go4lunch.domain.location.GpsLocationRepository;
import com.bakjoul.go4lunch.domain.location.LocationPermissionRepository;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainViewModelTest {

    private static final String USER_NOT_LOGGED = "Utilisateur non authentifié";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final Context context = Mockito.mock(Context.class);
    private final GetCurrentUserUseCase getCurrentUserUseCase = Mockito.mock(GetCurrentUserUseCase.class);
    private final LogOutUseCase logOutUseCase = Mockito.mock(LogOutUseCase.class);
    private final IsUserAuthenticatedUseCase isUserAuthenticatedUseCase = Mockito.mock(IsUserAuthenticatedUseCase.class);
    private final GetAutocompletePredictionsUseCase getAutocompletePredictionsUseCase = Mockito.mock(GetAutocompletePredictionsUseCase.class);
    private final GpsLocationRepository gpsLocationRepository = Mockito.mock(GpsLocationRepository.class);
    private final LocationPermissionRepository locationPermissionRepository = Mockito.mock(LocationPermissionRepository.class);
    private final AutocompleteRepository autocompleteRepository = Mockito.mock(AutocompleteRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final LoggedUserEntity fakeUser = Mockito.mock(LoggedUserEntity.class);

    private final MutableLiveData<Boolean> isLocationPermissionEnabledLiveData = new MutableLiveData<>();
    private final MutableLiveData<UserGoingToRestaurantEntity> userChosenRestaurantLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userSearchLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<PredictionResponse>> predictionsLiveData = new MutableLiveData<>();

    private MainViewModel viewModel;

    @Before
    public void setUp() {
        doReturn(USER_NOT_LOGGED).when(context).getString(R.string.main_user_not_logged);

        isLocationPermissionEnabledLiveData.setValue(true);
        doReturn(isLocationPermissionEnabledLiveData).when(locationPermissionRepository).getLocationPermissionLiveData();

        doReturn(fakeUser).when(getCurrentUserUseCase).invoke();
        doReturn("fakeUrl").when(fakeUser).getPhotoUrl();
        doReturn("fakeUsername").when(fakeUser).getUsername();
        doReturn("fakeEmail").when(fakeUser).getEmail();

        doReturn(userChosenRestaurantLiveData).when(userRepository).getChosenRestaurantLiveData(any(LoggedUserEntity.class));
        doReturn(predictionsLiveData).when(getAutocompletePredictionsUseCase).invoke(anyString());

        doReturn(userSearchLiveData).when(autocompleteRepository).getUserSearchLiveData();
    }

    @Test
    public void user_null_should_expose_null_main_view_state() {
        // Given
        doReturn(false).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();

        // When
        MainViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMainViewStateMediatorLiveData());

        // Then
        assertEquals(getUserNotLoggedViewState(), result);
    }

    @Test
    public void initial_case() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        userChosenRestaurantLiveData.setValue(null);
        predictionsLiveData.setValue(null);

        // When
        MainViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMainViewStateMediatorLiveData());

        // Then
        verify(userRepository).createFirestoreUser(any(LoggedUserEntity.class));
        assertEquals(getDefaultMainViewState(), result);
    }

    @Test
    public void user_going_to_restaurant_should_expose_chosen_restaurant_id_in_view_state() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        userChosenRestaurantLiveData.setValue(
            new UserGoingToRestaurantEntity(
                "fakeId",
                "fakeUsername",
                "fakeEmail",
                "fakePhotoUrl",
                "fakeChosenRestaurantId",
                "fakeRestaurantName",
                "fakeRestaurantAddress"
            )
        );

        // When
        MainViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMainViewStateMediatorLiveData());

        // Then
        assertEquals(getExpectedViewStateWithChosenRestaurant(), result);
    }

    @Test
    public void matching_user_search_should_expose_matching_suggestions_in_view_state() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        viewModel.onSearchViewQueryTextChanged("fakeRestaurant");
        predictionsLiveData.setValue(
            Collections.singletonList(
                new PredictionResponse(
                    "fakeRestaurant",
                    new ArrayList<>(),
                    "fakeRestaurantId",
                    "fakeReference",
                    new StructuredFormattingResponse("fakeRestaurantName", new ArrayList<>(), ""),
                    new ArrayList<>(),
                    Collections.singletonList("restaurant")
                )
            )
        );

        // When
        MainViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getMainViewStateMediatorLiveData());

        // Then
        assertEquals(getExpectedViewStateForFakeRestaurantSearch(), result);
    }

    @Test
    public void onBottomNavigationViewButtonClicked_should_update_dedicated_livedata() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        viewModel.onBottomNavigationClicked(MainViewModel.BottomNavigationViewButton.RESTAURANTS);

        // When
        MainViewModel.FragmentToDisplay result = LiveDataTestUtil.getValueForTesting(viewModel.getFragmentToDisplaySingleLiveEvent());

        // Then
        assertEquals(MainViewModel.FragmentToDisplay.RESTAURANTS, result);
    }

    @Test
    public void locationPermission_null_shoud_expose_fragmentToDisplay_null() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        isLocationPermissionEnabledLiveData.setValue(null);

        // When
        MainViewModel.FragmentToDisplay result = LiveDataTestUtil.getValueForTesting(viewModel.getFragmentToDisplaySingleLiveEvent());

        // Then
        assertNull(result);
    }

    @Test
    public void locationPermission_disabled_shoud_expose_fragmentToDisplay_no_permission() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        isLocationPermissionEnabledLiveData.setValue(false);

        // When
        MainViewModel.FragmentToDisplay result = LiveDataTestUtil.getValueForTesting(viewModel.getFragmentToDisplaySingleLiveEvent());

        // Then
        assertEquals(MainViewModel.FragmentToDisplay.NO_PERMISSION, result);
    }

    @Test
    public void locationPermission_enabled_shoud_expose_fragmentToDisplay_map() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        isLocationPermissionEnabledLiveData.setValue(true);

        // When
        MainViewModel.FragmentToDisplay result = LiveDataTestUtil.getValueForTesting(viewModel.getFragmentToDisplaySingleLiveEvent());

        // Then
        assertEquals(MainViewModel.FragmentToDisplay.MAP, result);
    }

    @Test
    public void locationPermission_enabled_and_bottomNavViewBtn_workmates_shoud_expose_fragmentToDisplay_workmates() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        isLocationPermissionEnabledLiveData.setValue(true);
        viewModel.onBottomNavigationClicked(MainViewModel.BottomNavigationViewButton.WORKMATES);

        // When
        MainViewModel.FragmentToDisplay result = LiveDataTestUtil.getValueForTesting(viewModel.getFragmentToDisplaySingleLiveEvent());

        // Then
        assertEquals(MainViewModel.FragmentToDisplay.WORKMATES, result);
    }

    @Test
    public void bottomNavViewBtn_null_error_should_expose_fragmentToDisplay_null() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();
        viewModel.onBottomNavigationClicked(null);

        // When
        MainViewModel.FragmentToDisplay result = LiveDataTestUtil.getValueForTesting(viewModel.getFragmentToDisplaySingleLiveEvent());

        // Then
        assertNull(result);
    }

    @Test
    public void onSuggestionClicked_should_set_user_search_livedata_in_autocomplete_repo() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        initViewModel();

        viewModel.onSuggestionClicked("suggestion_example");
        // User search livedata returned by autocompleteRepository
        userSearchLiveData.setValue("suggestion_example");

        // When
        String result = LiveDataTestUtil.getValueForTesting(autocompleteRepository.getUserSearchLiveData());

        // Then
        assertEquals("suggestion_example", result);
    }

    @Test
    public void verify_onResume_with_permission() {
        // Given
        doReturn(PackageManager.PERMISSION_GRANTED).when(context).checkPermission(
            eq(Manifest.permission.ACCESS_FINE_LOCATION),
            anyInt(),
            anyInt()
        );
        initViewModel();

        // When
        viewModel.onResume();

        // Then
        Mockito.verify(gpsLocationRepository).startLocationUpdates();
        Mockito.verify(locationPermissionRepository).setLocationPermission(true);
        Mockito.verifyNoMoreInteractions(gpsLocationRepository, locationPermissionRepository);
    }

    @Test
    public void verify_onResume_without_permission() {
        // Given
        doReturn(PackageManager.PERMISSION_DENIED).when(context).checkPermission(
            eq(Manifest.permission.ACCESS_FINE_LOCATION),
            anyInt(),
            anyInt()
        );
        initViewModel();

        // When
        viewModel.onResume();

        // Then
        Mockito.verify(gpsLocationRepository).stopLocationUpdates();
        Mockito.verify(locationPermissionRepository).setLocationPermission(false);
        Mockito.verifyNoMoreInteractions(gpsLocationRepository, locationPermissionRepository);
    }

    @Test
    public void logOut() {
        // Given
        initViewModel();

        // When
        viewModel.logOut();

        // Then
        Mockito.verify(logOutUseCase).invoke();
        verifyNoMoreInteractions(logOutUseCase);
    }

    // region IN
    private void initViewModel() {
        viewModel = new MainViewModel(
            context,
            getCurrentUserUseCase,
            logOutUseCase,
            isUserAuthenticatedUseCase,
            getAutocompletePredictionsUseCase,
            gpsLocationRepository,
            locationPermissionRepository,
            autocompleteRepository,
            userRepository
        );

        Mockito.verify(locationPermissionRepository).getLocationPermissionLiveData();
    }
    // endregion IN

    // region OUT
    @NonNull
    private MainViewState getUserNotLoggedViewState() {
        return new MainViewState(null, "Utilisateur non authentifié", "", null, new ArrayList<>());
    }

    @NonNull
    private MainViewState getDefaultMainViewState() {
        return new MainViewState("fakeUrl", "fakeUsername", "fakeEmail", null, new ArrayList<>());
    }

    @NonNull
    private MainViewState getExpectedViewStateWithChosenRestaurant() {
        return new MainViewState("fakeUrl", "fakeUsername", "fakeEmail", "fakeChosenRestaurantId", new ArrayList<>());
    }

    @NonNull
    private MainViewState getExpectedViewStateForFakeRestaurantSearch() {
        return new MainViewState("fakeUrl", "fakeUsername", "fakeEmail", null, Collections.singletonList("fakeRestaurantName"));
    }
    // endregion OUT
}