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
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.bakjoul.go4lunch.ui.dispatcher.DispatcherActivity;
import com.bakjoul.go4lunch.ui.main.MainViewModel.BottomNavigationViewButton;
import com.bakjoul.go4lunch.ui.main.MainViewModel.FragmentToDisplay;
import com.bakjoul.go4lunch.ui.map.MapFragment;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkLocationPermission();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setToolbar();
        setBottomNavigationView();
        DrawerLayout drawerLayout = binding.mainDrawerLayout;
        drawerLayout.setStatusBarBackground(R.color.primaryDarkColor);

        View header = binding.mainNavigationView.getHeaderView(0);
        AppCompatImageView photo = header.findViewById(R.id.main_drawer_user_photo);
        TextView username = header.findViewById(R.id.main_drawer_user_name);
        TextView email = header.findViewById(R.id.main_drawer_user_email);

        viewModel.getMainActivityViewStateLiveData().observe(this, viewState -> {
            Glide.with(photo.getContext())
                .load(viewState.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(photo);
            username.setText(viewState.getUsername());
            email.setText(viewState.getEmail());

            setMainNavigationView(viewState);
        });

        viewModel.getFragmentToDisplaySingleLiveEvent().observe(this, this::displayFragment);
    }

    @SuppressLint("NonConstantResourceId")
    private void setMainNavigationView(MainViewState viewState) {
        binding.mainNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mainNavigationDrawer_menu_lunch:
                    if (viewState.getChosenRestaurantId() != null) {
                        DetailsActivity.navigate(viewState.getChosenRestaurantId(), this);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.main_toast_lunch, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.mainNavigationDrawer_menu_settings:
                    break;
                case R.id.mainNavigationDrawer_menu_logout:
                    viewModel.logOut();
                    startActivity(new Intent(this, DispatcherActivity.class));
                    finish();
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.onResume();
    }

    private void setToolbar() {
        Toolbar toolbar = binding.mainToolbar;
        toolbar.setTitle(R.string.toolbar_title_hungry);
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
            if (fragment instanceof MapFragment) {
                if (selected == FragmentToDisplay.MAP) {
                    setToolbarTitle(selected);
                    transaction.show(fragment);
                    shown = true;
                } else {
                    transaction.hide(fragment);
                }
            } else if (fragment instanceof RestaurantsFragment) {
                if (selected == FragmentToDisplay.RESTAURANTS) {
                    setToolbarTitle(selected);
                    transaction.show(fragment);
                    shown = true;
                } else {
                    transaction.hide(fragment);
                }
            } else if (fragment instanceof WorkmatesFragment) {
                if (selected == FragmentToDisplay.WORKMATES) {
                    setToolbarTitle(selected);
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
                    transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), MapFragment.newInstance());
                    break;
                case RESTAURANTS:
                    binding.mainToolbar.setTitle(R.string.toolbar_title_hungry);
                    transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), RestaurantsFragment.newInstance());
                    break;
                case WORKMATES:
                    binding.mainToolbar.setTitle(R.string.toolbar_title_workmates);
                    transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), WorkmatesFragment.newInstance());
                    break;
                case NO_PERMISSION:
                    transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), NoPermissionFragment.newInstance());
                    break;
            }
        }

        transaction.commitNow();

        isBottomNavigationViewListenerDisabled = true;
        binding.mainBottomNavigationView.setSelectedItemId(selected.getMenuItemId());
        isBottomNavigationViewListenerDisabled = false;
    }

    private void setToolbarTitle(@NonNull FragmentToDisplay selected) {
        switch (selected) {
            case MAP:
            case RESTAURANTS:
                if (binding.mainToolbar.getTitle() != getString(R.string.toolbar_title_hungry)) {
                    binding.mainToolbar.setTitle(R.string.toolbar_title_hungry);
                }
                break;
            case WORKMATES:
                if (binding.mainToolbar.getTitle() != getString(R.string.toolbar_title_workmates)) {
                    binding.mainToolbar.setTitle(R.string.toolbar_title_workmates);
                }
                break;
        }
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
