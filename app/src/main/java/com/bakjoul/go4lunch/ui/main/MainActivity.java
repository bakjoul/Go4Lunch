package com.bakjoul.go4lunch.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivityMainBinding;
import com.bakjoul.go4lunch.ui.dispatcher.DispatcherActivity;
import com.bakjoul.go4lunch.ui.map.MapFragment;
import com.bakjoul.go4lunch.ui.restaurants.RestaurantsFragment;
import com.bakjoul.go4lunch.ui.workmates.WorkmatesFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        if (savedInstanceState == null) {
            displayFragment(BottomNavigationViewFragment.MAP);
        }
    }

    private void setToolbar() {
        Toolbar toolbar = binding.mainToolbar;
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setBackgroundColor(getColor(R.color.primaryColor));
        }
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
            displayFragment(BottomNavigationViewFragment.fromMenuId(item.getItemId()));
            return true;
        });
    }

    private void displayFragment(BottomNavigationViewFragment selected) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean shown = false;

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof MapFragment) {
                if (selected == BottomNavigationViewFragment.MAP) {
                    transaction.show(fragment);
                    shown = true;
                } else {
                    transaction.hide(fragment);
                }
            } else if (fragment instanceof RestaurantsFragment) {
                if (selected == BottomNavigationViewFragment.RESTAURANTS) {
                    transaction.show(fragment);
                    shown = true;
                } else {
                    transaction.hide(fragment);
                }
            } else if (fragment instanceof WorkmatesFragment) {
                if (selected == BottomNavigationViewFragment.WORKMATES) {
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
                    transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), RestaurantsFragment.newInstance());
                    break;
                case WORKMATES:
                    transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), WorkmatesFragment.newInstance());
                    break;
            }
        }

        transaction.commit();
    }

    private enum BottomNavigationViewFragment {
        MAP(R.id.bottomNavigationView_menu_mapView),
        RESTAURANTS(R.id.bottomNavigationView_menu_listView),
        WORKMATES(R.id.bottomNavigationView_menu_workmates);

        @IdRes
        private final int menuId;

        BottomNavigationViewFragment(@IdRes int menuId) {
            this.menuId = menuId;
        }

        @NonNull
        public static BottomNavigationViewFragment fromMenuId(@IdRes int menuId) {
            for (BottomNavigationViewFragment value : BottomNavigationViewFragment.values()) {
                if (value.menuId == menuId) {
                    return value;
                }
            }

            throw new IllegalStateException("Unknown menuId: " + menuId);
        }
    }
}
