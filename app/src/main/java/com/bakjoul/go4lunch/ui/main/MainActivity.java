package com.bakjoul.go4lunch.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivityMainBinding;
import com.bakjoul.go4lunch.ui.NoPermissionFragment;
import com.bakjoul.go4lunch.ui.dispatcher.DispatcherActivity;
import com.bakjoul.go4lunch.ui.main.MainViewModel.BottomNavigationViewButton;
import com.bakjoul.go4lunch.ui.main.MainViewModel.FragmentToDisplay;
import com.bakjoul.go4lunch.ui.map.MapFragment2;
import com.bakjoul.go4lunch.ui.restaurants.RestaurantsFragment;
import com.bakjoul.go4lunch.ui.workmates.WorkmatesFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

   private ActivityMainBinding binding;
   private MainViewModel viewModel;

   private boolean isBottomNavigationViewListenerDisabled = false;

   @SuppressLint("NonConstantResourceId")
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      binding = ActivityMainBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());

      checkLocationPermission();

      viewModel = new ViewModelProvider(this).get(MainViewModel.class);

      View header = binding.mainNavigationView.getHeaderView(0);
      AppCompatImageView photo = header.findViewById(R.id.main_drawer_user_photo);
      TextView username = header.findViewById(R.id.main_drawer_user_name);
      TextView email = header.findViewById(R.id.main_drawer_user_email);

      setToolbar();
      setDrawer();
      setBottomNavigationView();

      viewModel.getMainActivityViewStateLiveData().observe(this, mainViewState -> {
         Glide.with(photo.getContext())
             .load(mainViewState.getPhotoUrl())
             .apply(RequestOptions.circleCropTransform())
             .into(photo);
         username.setText(mainViewState.getUsername());
         email.setText(mainViewState.getEmail());
      });

      viewModel.getFragmentToDisplaySingleLiveEvent().observe(this, this::displayFragment);
   }

   @Override
   protected void onResume() {
      super.onResume();

      viewModel.onResume();
   }

   private void setToolbar() {
      Toolbar toolbar = binding.mainToolbar;
      setSupportActionBar(toolbar);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         toolbar.setBackgroundColor(getColor(R.color.primaryColor));
      }
      binding.mainToolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
      binding.mainToolbar.setNavigationIconTint(getResources().getColor(R.color.white));
      binding.mainToolbar.setNavigationOnClickListener(view ->
          binding.mainDrawerLayout.openDrawer(GravityCompat.START)
      );
   }

   @SuppressLint("NonConstantResourceId")
   private void setDrawer() {
      DrawerLayout drawerLayout = binding.mainDrawerLayout;
      drawerLayout.setStatusBarBackground(R.color.primaryDarkColor);

      binding.mainNavigationView.setNavigationItemSelectedListener(item -> {
         switch (item.getItemId()) {
            case R.id.mainNavigationDrawer_menu_lunch:
               break;
            case R.id.mainNavigationDrawer_menu_settings:
               break;
            case R.id.mainNavigationDrawer_menu_logout:
               viewModel.logOut();
               startActivity(new Intent(MainActivity.this, DispatcherActivity.class));
               finish();
               break;
         }
         return true;
      });
   }

   private void setBottomNavigationView() {
      binding.mainBottomNavigationView.setOnItemSelectedListener(item -> {
         if (!isBottomNavigationViewListenerDisabled) {
            viewModel.onBottomNavigationClicked(BottomNavigationViewButton.fromMenuId(item.getItemId()));
         }
         return true;
      });
   }

   private void displayFragment(FragmentToDisplay selected) {
      Log.d("Nino", "displayFragment() called with: selected = [" + selected + "]");
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      boolean shown = false;

      for (Fragment fragment : getSupportFragmentManager().getFragments()) {
         if (fragment instanceof MapFragment2) {
            if (selected == FragmentToDisplay.MAP) {
               transaction.show(fragment);
               shown = true;
            } else {
               transaction.hide(fragment);
            }
         } else if (fragment instanceof RestaurantsFragment) {
            if (selected == FragmentToDisplay.RESTAURANT) {
               transaction.show(fragment);
               shown = true;
            } else {
               transaction.hide(fragment);
            }
         } else if (fragment instanceof WorkmatesFragment) {
            if (selected == FragmentToDisplay.WORKMATES) {
               transaction.show(fragment);
               shown = true;
            } else {
               transaction.hide(fragment);
            }
         } else if (fragment instanceof NoPermissionFragment) {
            if (selected == FragmentToDisplay.NO_PERMISSION) {
               transaction.show(fragment);
               shown = true;
            } else {
               transaction.hide(fragment);
            }
         }
      }

      if (!shown) {
         switch (selected) {
            case MAP:
               transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), MapFragment2.newInstance());
               break;
            case RESTAURANT:
               transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), RestaurantsFragment.newInstance());
               break;
            case WORKMATES:
               transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), WorkmatesFragment.newInstance());
               break;
            case NO_PERMISSION:
               transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), NoPermissionFragment.newInstance());
               break;
         }
      }

      transaction.commit();

      isBottomNavigationViewListenerDisabled = true;
      binding.mainBottomNavigationView.setSelectedItemId(selected.getMenuItemId());
      isBottomNavigationViewListenerDisabled = false;
   }

   private void checkLocationPermission() {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
         showRequestPermissionRationale();
      } else {
         requestLocationPermission();
      }
   }

   private void showRequestPermissionRationale() {
      new AlertDialog.Builder(this)
          .setTitle(getString(R.string.permission_rationale_title))
          .setMessage(getString(R.string.permission_rationale_message))
          .setPositiveButton(getString(R.string.permission_rationale_positive_button), (dialogInterface, i) -> requestLocationPermission())
          .setNegativeButton(getString(R.string.permission_rationale_negative_button), (dialogInterface, i) -> dialogInterface.dismiss())
          .create()
          .show();
   }

   private void requestLocationPermission() {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
   }
}
