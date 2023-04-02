package com.bakjoul.go4lunch.domain.workmate;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetAvailableWorkmatesUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GetCurrentUserUseCase getCurrentUserUseCase = Mockito.mock(GetCurrentUserUseCase.class);

    private final WorkmateRepository workmatesRepository = Mockito.mock(WorkmateRepository.class);

    private final LoggedUserEntity mockedUser = Mockito.mock(LoggedUserEntity.class);

    private final MutableLiveData<List<WorkmateEntity>> workmatesLiveData = new MutableLiveData<>();

    private GetAvailableWorkmatesUseCase getAvailableWorkmatesUseCase;

    @Before
    public void setUp() {
        doReturn(mockedUser).when(getCurrentUserUseCase).invoke();
        doReturn("fakeId").when(mockedUser).getId();
        doReturn(workmatesLiveData).when(workmatesRepository).getAvailableWorkmatesLiveData();

        getAvailableWorkmatesUseCase = new GetAvailableWorkmatesUseCase(getCurrentUserUseCase, workmatesRepository);
    }

    @Test
    public void invoke_should_return_workmates_list() {
        // Given
        workmatesLiveData.setValue(getDefaultWorkmatesList());

        // When
        List<WorkmateEntity> result = LiveDataTestUtil.getValueForTesting(getAvailableWorkmatesUseCase.invoke());

        // Then
        assertEquals(getExpectedWorkmatesList(), result);
    }

    @Test
    public void invoke_should_return_null_when_current_user_is_null() {
        // Given
        doReturn(null).when(getCurrentUserUseCase).invoke();

        // When
        List<WorkmateEntity> result = LiveDataTestUtil.getValueForTesting(getAvailableWorkmatesUseCase.invoke());

        // Then
        assertNull(result);
    }

    // region IN
    @NonNull
    private List<WorkmateEntity> getDefaultWorkmatesList() {
        return Arrays.asList(
            new WorkmateEntity("1", "fakeWorkmate1", "fakeWorkmateEmail1", null),
            new WorkmateEntity("fakeId", "currentUser", "currentUserEmail2", null)
        );
    }
    // endregion IN

    // region OUT
    @NonNull
    private List<WorkmateEntity> getExpectedWorkmatesList() {
        return Collections.singletonList(
            new WorkmateEntity("1", "fakeWorkmate1", "fakeWorkmateEmail1", null)
        );
    }
    // endregion OUT
}