package com.bakjoul.go4lunch.ui.main;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.data.repository.GpsLocationRepository;
import com.bakjoul.go4lunch.data.repository.PermissionRepository;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mockito;

public class MainViewModelTest {

   @Rule
   public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

   private final Context context = Mockito.mock(Context.class);
   private final FirebaseAuth firebaseAuth = Mockito.mock(FirebaseAuth.class);
   private final GpsLocationRepository gpsLocationRepository = Mockito.mock(GpsLocationRepository.class);
   private final PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
   
   private MainViewModel viewModel;

   @Before
   public void setUp() {
      viewModel = new MainViewModel(context, firebaseAuth, gpsLocationRepository, permissionRepository);
   }

}