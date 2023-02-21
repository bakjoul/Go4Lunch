package com.bakjoul.go4lunch.ui.workmates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateEntity;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorkmatesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final WorkmateRepositoryImplementation workmateRepositoryImplementation = Mockito.mock(WorkmateRepositoryImplementation.class);
    private final AutocompleteRepository autocompleteRepository = Mockito.mock(AutocompleteRepository.class);

    private final MutableLiveData<List<WorkmateEntity>> availableWorkmatesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<UserGoingToRestaurantEntity>> workmatesGoingToRestaurantsLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> userSearchLiveData = new MutableLiveData<>();

    private WorkmatesViewModel viewModel;

    @Before
    public void setUp() {
        doReturn(availableWorkmatesLiveData).when(workmateRepositoryImplementation).getAvailableWorkmatesLiveData();
        doReturn(workmatesGoingToRestaurantsLiveData).when(workmateRepositoryImplementation).getWorkmatesGoingToRestaurantsLiveData();

        userSearchLiveData.setValue(null);
        doReturn(userSearchLiveData).when(autocompleteRepository).getUserSearchLiveData();

        viewModel = new WorkmatesViewModel(workmateRepositoryImplementation, autocompleteRepository);
    }

    @Test
    public void initial_case() {
        // Given
        availableWorkmatesLiveData.setValue(new ArrayList<>());
        workmatesGoingToRestaurantsLiveData.setValue(new ArrayList<>());

        // When
        WorkmatesViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getWorkmatesViewStateMediatorLiveData());

        // Then
        assertEquals(getDefaultWorkmatesViewState(), result);
    }

    @Test
    public void availableWorkmates_and_workmatesGoingToRestaurants_null_should_expose_viewstate_null() {
        availableWorkmatesLiveData.setValue(null);
        workmatesGoingToRestaurantsLiveData.setValue(null);

        // When
        WorkmatesViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getWorkmatesViewStateMediatorLiveData());

        // Then
        assertNull(result);
    }

    @Test
    public void availableWorkmates_null_and_workmatesGoingToRestaurants_empty_should_expose_viewstate_null() {
        // Given
        availableWorkmatesLiveData.setValue(null);
        workmatesGoingToRestaurantsLiveData.setValue(new ArrayList<>());

        // When
        WorkmatesViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getWorkmatesViewStateMediatorLiveData());

        // Then
        assertNull(result);
    }

    @Test
    public void availableWorkmates_empty_and_workmatesGoingToRestaurants_null_should_expose_viewstate_null() {
        // Given
        availableWorkmatesLiveData.setValue(new ArrayList<>());
        workmatesGoingToRestaurantsLiveData.setValue(null);

        // When
        WorkmatesViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getWorkmatesViewStateMediatorLiveData());

        // Then
        assertNull(result);
    }

    @Test
    public void availableWorkmates_and_workmatesGoingToRestaurants_should_be_exposed_in_viewstate_list() {
        // Given
        availableWorkmatesLiveData.setValue(getDefaultAvailableWorkmates());
        workmatesGoingToRestaurantsLiveData.setValue(new ArrayList<>(
                Collections.singletonList(
                    new UserGoingToRestaurantEntity(
                        "1",
                        "fakeUsername1",
                        "fakeUserEmail1",
                        "fakeUserPhotoUrl1",
                        "fakeRestaurantId",
                        "fakeRestaurantName",
                        "fakeRestaurantAddress"
                    )
                )
            )
        );

        // When
        WorkmatesViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getWorkmatesViewStateMediatorLiveData());

        // Then
        assertEquals(getExpectedWorkmatesViewStateList(), result.getWorkmatesItemViewStateList());
    }

    @Test
    public void matched_user_search_case() {
        // Given
        availableWorkmatesLiveData.setValue(getDefaultAvailableWorkmates());
        workmatesGoingToRestaurantsLiveData.setValue(new ArrayList<>(
                Collections.singletonList(
                    new UserGoingToRestaurantEntity(
                        "3",
                        "fakeUsername3",
                        "fakeUserEmail3",
                        "fakeUserPhotoUrl3",
                        "fakeRestaurantId",
                        "searchedRestaurantName",
                        "fakeRestaurantAddress"
                    )
                )
            )
        );
        userSearchLiveData.setValue("searchedRestaurant");

        // When
        WorkmatesViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getWorkmatesViewStateMediatorLiveData());
        Boolean isSearchUnmatched = LiveDataTestUtil.getValueForTesting(viewModel.getIsUserSearchUnmatchedSingleLiveEvent());

        // Then
        assertEquals(getExpectedSearchedWorkmatesViewStateList(), result.getWorkmatesItemViewStateList());
        assertEquals(false, isSearchUnmatched);
    }

    @Test
    public void unmatched_user_search_case() {
        // Given
        availableWorkmatesLiveData.setValue(getDefaultAvailableWorkmates());
        workmatesGoingToRestaurantsLiveData.setValue(new ArrayList<>(
                Collections.singletonList(
                    new UserGoingToRestaurantEntity(
                        "1",
                        "fakeUsername1",
                        "fakeUserEmail1",
                        "fakeUserPhotoUrl1",
                        "fakeRestaurantId",
                        "fakeRestaurantName",
                        "fakeRestaurantAddress"
                    )
                )
            )
        );
        userSearchLiveData.setValue("abcdef");

        // When
        WorkmatesViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getWorkmatesViewStateMediatorLiveData());
        Boolean isSearchUnmatched = LiveDataTestUtil.getValueForTesting(viewModel.getIsUserSearchUnmatchedSingleLiveEvent());

        // Then
        assertEquals(getExpectedWorkmatesViewStateList(), result.getWorkmatesItemViewStateList());
        assertEquals(true, isSearchUnmatched);
    }

    // region IN
    @NonNull
    private List<WorkmateEntity> getDefaultAvailableWorkmates() {
        return new ArrayList<>(
            Arrays.asList(
                new WorkmateEntity("1", "fakeUsername1", "fakeUserEmail1", "fakePhotoUrl1"),
                new WorkmateEntity("2", "fakeUsername2", "fakeUserEmail2", "fakePhotoUrl2"),
                new WorkmateEntity("3", "fakeUsername3", "fakeUserEmail3", "fakePhotoUrl3")
            )
        );
    }
    // endregion IN

    // region OUT
    @NonNull
    private WorkmatesViewState getDefaultWorkmatesViewState() {
        return new WorkmatesViewState(
            new ArrayList<>(),
            true
        );
    }

    @NonNull
    private List<WorkmateItemViewState> getExpectedWorkmatesViewStateList() {
        return new ArrayList<>(
            Arrays.asList(
                new WorkmateItemViewState("1", "fakePhotoUrl1", "fakeUsername1", "fakeRestaurantId", "fakeRestaurantName", false),
                new WorkmateItemViewState("2", "fakePhotoUrl2", "fakeUsername2", null, null, false),
                new WorkmateItemViewState("3", "fakePhotoUrl3", "fakeUsername3", null, null, false)
            )
        );
    }

    @NonNull
    private List<WorkmateItemViewState> getExpectedSearchedWorkmatesViewStateList() {
        return new ArrayList<>(
            Arrays.asList(
                new WorkmateItemViewState("3", "fakePhotoUrl3", "fakeUsername3", "fakeRestaurantId", "searchedRestaurantName", true),
                new WorkmateItemViewState("1", "fakePhotoUrl1", "fakeUsername1", null, null, false),
                new WorkmateItemViewState("2", "fakePhotoUrl2", "fakeUsername2", null, null, false)
            )
        );
    }
    // endregion OUT
}
