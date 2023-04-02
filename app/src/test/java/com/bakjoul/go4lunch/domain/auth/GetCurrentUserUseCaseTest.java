package com.bakjoul.go4lunch.domain.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class GetCurrentUserUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final AuthRepository authRepository = Mockito.mock(AuthRepository.class);

    private final LoggedUserEntity mockedUser = Mockito.mock(LoggedUserEntity.class);

    private GetCurrentUserUseCase getCurrentUserUseCase;

    @Before
    public void setUp() {
        getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
    }

    @Test
    public void current_user_null_should_return_null() {
        // Given
        doReturn(null).when(authRepository).getCurrentUser();

        // When
        LoggedUserEntity result = getCurrentUserUseCase.invoke();

        // Then
        assertNull(result);
    }

    @Test
    public void current_user_not_null_should_return_user() {
        // Given
        doReturn(mockedUser).when(authRepository).getCurrentUser();

        // When
        LoggedUserEntity result = getCurrentUserUseCase.invoke();

        // Then
        assertEquals(mockedUser, result);
    }
}
