package com.bakjoul.go4lunch.domain.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GetWorkmatesGoingToRestaurantsUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GetCurrentUserUseCase getCurrentUserUseCase = Mockito.mock(GetCurrentUserUseCase.class);

    private final WorkmateRepository workmatesRepository = Mockito.mock(WorkmateRepository.class);

    private final LoggedUserEntity mockedUser = Mockito.mock(LoggedUserEntity.class);

    private final MutableLiveData<List<UserGoingToRestaurantEntity>> workmatesLiveData = new MutableLiveData<>();

    private GetWorkmatesGoingToRestaurantsUseCase getWorkmatesGoingToRestaurantsUseCase;

    @Before
    public void setUp() {
        doReturn(mockedUser).when(getCurrentUserUseCase).invoke();
        doReturn("currentUserId").when(mockedUser).getId();
        doReturn(workmatesLiveData).when(workmatesRepository).getWorkmatesGoingToRestaurantsLiveData();

        getWorkmatesGoingToRestaurantsUseCase = new GetWorkmatesGoingToRestaurantsUseCase(getCurrentUserUseCase, workmatesRepository);
    }

    @Test
    public void invoke_should_return_null_when_current_user_is_null() {
        // Given
        Mockito.doReturn(null).when(getCurrentUserUseCase).invoke();

        // When
        Collection<String> result = LiveDataTestUtil.getValueForTesting(getWorkmatesGoingToRestaurantsUseCase.invoke());

        // Then
        assertNull(result);
    }

    @Test
    public void invoke_should_return_restaurants_list() {
        // Given
        workmatesLiveData.setValue(getDefaultWorkmatesList());

        // When
        Collection<String> result = LiveDataTestUtil.getValueForTesting(getWorkmatesGoingToRestaurantsUseCase.invoke());

        // Then
        assertEquals(getExpectedRestaurantsList(), result);
    }

    // region IN
    @NonNull
    private List<UserGoingToRestaurantEntity> getDefaultWorkmatesList() {
        return Arrays.asList(
            new UserGoingToRestaurantEntity("currentUserId", "currentUserName", "currentUserEmail", "currentUserPhotoUrl", "fakeRestaurantId1", "fakeRestaurantName1", "fakeRestaurantAddress"),
            new UserGoingToRestaurantEntity("fakeId2", "fakeName2", "fakeEmail2", "fakePhotoUrl2", "fakeRestaurantId2", "fakeRestaurantName2", "fakeRestaurantAddress"),
            new UserGoingToRestaurantEntity("fakeId3", "fakeName3", "fakeEmail3", "fakePhotoUrl3", "fakeRestaurantId3", "fakeRestaurantName3", "fakeRestaurantAddress"),
            new UserGoingToRestaurantEntity("fakeId4", "fakeName4", "fakeEmail4", "fakePhotoUrl4", "fakeRestaurantId4", "fakeRestaurantName4", "fakeRestaurantAddress")
        );
    }
    // endregion IN

    // region OUT
    @NonNull
    private Collection<String> getExpectedRestaurantsList() {
        return new HashSet<>(Arrays.asList("fakeRestaurantId2", "fakeRestaurantId3", "fakeRestaurantId4"));
    }
    // endregion OUT

}