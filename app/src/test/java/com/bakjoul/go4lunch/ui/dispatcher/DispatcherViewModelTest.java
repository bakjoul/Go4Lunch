package com.bakjoul.go4lunch.ui.dispatcher;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.domain.auth.IsUserAuthenticatedUseCase;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class DispatcherViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final IsUserAuthenticatedUseCase isUserAuthenticatedUseCase = Mockito.mock(IsUserAuthenticatedUseCase.class);

    private DispatcherViewModel viewModel;

    @Test
    public void currentUser_null_should_expose_go_to_connect_screen_view_action() {
        // Given
        doReturn(false).when(isUserAuthenticatedUseCase).invoke();
        viewModel = new DispatcherViewModel(isUserAuthenticatedUseCase);

        // When
        DispatcherViewAction result = LiveDataTestUtil.getValueForTesting(viewModel.getViewActionSingleLiveEvent());

        // Then
        assertEquals(DispatcherViewAction.GO_TO_CONNECT_SCREEN, result);
    }

    @Test
    public void currentUser_loggedIn_should_expose_go_to_main_screen_view_action() {
        // Given
        doReturn(true).when(isUserAuthenticatedUseCase).invoke();
        viewModel = new DispatcherViewModel(isUserAuthenticatedUseCase);

        // When
        DispatcherViewAction result = LiveDataTestUtil.getValueForTesting(viewModel.getViewActionSingleLiveEvent());

        // Then
        assertEquals(DispatcherViewAction.GO_TO_MAIN_SCREEN, result);
    }
}