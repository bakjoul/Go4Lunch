package com.bakjoul.go4lunch.ui.dispatcher;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class DispatcherViewModelTest {

   @Rule
   public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

   private final FirebaseAuth firebaseAuth = Mockito.mock(FirebaseAuth.class);

   private DispatcherViewModel viewModel;

   @Before
   public void setUp() {
      viewModel = new DispatcherViewModel(firebaseAuth);
   }

   @Test
   public void currentUser_null_should_expose_go_to_connect_screen_view_action() {
      // Given
      doReturn(null).when(firebaseAuth).getCurrentUser();

      // When
      DispatcherViewAction result = viewModel.getViewActionSingleLiveEvent().getValue();

      // Then
      assertEquals(DispatcherViewAction.GO_TO_CONNECT_SCREEN, result);
   }

/*   @Test
   public void currentUser_non_null_should_expose_go_to_main_screen_view_action() {
      // Given
      doReturn().when(firebaseAuth).getCurrentUser();

      // When
      DispatcherViewAction result = viewModel.getViewActionSingleLiveEvent().getValue();

      // Then
      assertEquals(DispatcherViewAction.GO_TO_MAIN_SCREEN, result);
   }*/
}