package com.bakjoul.go4lunch.ui.dispatcher;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
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

}