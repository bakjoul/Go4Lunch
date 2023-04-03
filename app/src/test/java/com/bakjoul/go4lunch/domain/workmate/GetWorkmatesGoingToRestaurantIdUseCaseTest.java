package com.bakjoul.go4lunch.domain.workmate;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class GetWorkmatesGoingToRestaurantIdUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GetCurrentUserUseCase getCurrentUserUseCase = Mockito.mock(GetCurrentUserUseCase.class);

    private final WorkmateRepository workmatesRepository = Mockito.mock(WorkmateRepository.class);

    private final LoggedUserEntity mockedUser = Mockito.mock(LoggedUserEntity.class);

    private final MutableLiveData<List<UserGoingToRestaurantEntity>> workmatesLiveData = new MutableLiveData<>();

    private GetWorkmatesGoingToRestaurantIdUseCase getWorkmatesGoingToRestaurantIdUseCase;

    @Before
    public void setUp() {
        doReturn(mockedUser).when(getCurrentUserUseCase).invoke();
        doReturn("currentUserId").when(mockedUser).getId();
        doReturn(workmatesLiveData).when(workmatesRepository).getWorkmatesGoingToRestaurantIdLiveData(anyString());

        getWorkmatesGoingToRestaurantIdUseCase = new GetWorkmatesGoingToRestaurantIdUseCase(getCurrentUserUseCase, workmatesRepository);
    }

    @Test
    public void invoke_should_return_null_when_current_user_is_null() {
        // Given
        doReturn(null).when(getCurrentUserUseCase).invoke();

        // When
        List<UserGoingToRestaurantEntity> result = LiveDataTestUtil.getValueForTesting(getWorkmatesGoingToRestaurantIdUseCase.invoke("fakeRestaurantId"));

        // Then
        assertNull(result);
    }

    @Test
    public void invoke_should_return_workmates_going_to_restaurant_list() {
        // Given
        workmatesLiveData.setValue(getDefaultWorkmatesList());

        // When
        List<UserGoingToRestaurantEntity> result = LiveDataTestUtil.getValueForTesting(getWorkmatesGoingToRestaurantIdUseCase.invoke("fakeRestaurantId"));

        // Then
        assertEquals(getExpectedWorkmatesList(), result);
    }

    // region IN
    @NonNull
    private List<UserGoingToRestaurantEntity> getDefaultWorkmatesList() {
        return Arrays.asList(
            new UserGoingToRestaurantEntity("currentUserId", "currentUserName", "currentUserEmail", "currentUserPhotoUrl", "fakeRestaurantId", "fakeRestaurantName", "fakeRestaurantAddress"),
            new UserGoingToRestaurantEntity("fakeId2", "fakeName2", "fakeEmail2", "fakePhotoUrl2", "fakeRestaurantId", "fakeRestaurantName", "fakeRestaurantAddress"),
            new UserGoingToRestaurantEntity("fakeId3", "fakeName3", "fakeEmail3", "fakePhotoUrl3", "fakeRestaurantId", "fakeRestaurantName", "fakeRestaurantAddress")
        );
    }
    // endregion IN

    // region OUT
    @NonNull
    private List<UserGoingToRestaurantEntity> getExpectedWorkmatesList() {
        return Arrays.asList(
            new UserGoingToRestaurantEntity("fakeId2", "fakeName2", "fakeEmail2", "fakePhotoUrl2", "fakeRestaurantId", "fakeRestaurantName", "fakeRestaurantAddress"),
            new UserGoingToRestaurantEntity("fakeId3", "fakeName3", "fakeEmail3", "fakePhotoUrl3", "fakeRestaurantId", "fakeRestaurantName", "fakeRestaurantAddress")
        );
    }
    // endregion OUT
}