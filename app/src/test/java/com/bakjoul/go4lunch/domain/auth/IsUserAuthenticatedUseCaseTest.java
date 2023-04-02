package com.bakjoul.go4lunch.domain.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class IsUserAuthenticatedUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final FirebaseAuth firebaseAuth = Mockito.mock(FirebaseAuth.class);

    private final FirebaseUser mockedFirebaseUser = Mockito.mock(FirebaseUser.class);

    private IsUserAuthenticatedUseCase isUserAuthenticatedUseCase;

    @Before
    public void setUp() {
        isUserAuthenticatedUseCase = new IsUserAuthenticatedUseCase(firebaseAuth);
    }

    @Test
    public void current_user_null_should_return_false() {
        // Given
        doReturn(null).when(firebaseAuth).getCurrentUser();

        // When
        boolean result = isUserAuthenticatedUseCase.invoke();

        // Then
        assertFalse(result);
    }

    @Test
    public void current_user_not_null_should_return_true() {
        // Given
        doReturn(mockedFirebaseUser).when(firebaseAuth).getCurrentUser();

        // When
        boolean result = isUserAuthenticatedUseCase.invoke();

        // Then
        assertTrue(result);
    }
}
