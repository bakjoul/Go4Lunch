package com.bakjoul.go4lunch.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivityMainBinding;
import com.bakjoul.go4lunch.ui.map.MapFragment;
import com.bakjoul.go4lunch.ui.restaurants.RestaurantsFragment;
import com.bakjoul.go4lunch.ui.workmates.WorkmatesFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.mainToolbar;
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getColor(R.color.primaryColor));

        DrawerLayout drawerLayout = binding.mainDrawerLayout;
        drawerLayout.setStatusBarBackground(R.color.primaryDarkColor);

        setBottomNavigationView();

        if (savedInstanceState == null) {
            displayFragment(BottomNavigationViewFragment.MAP);
        }
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

    private void setBottomNavigationView() {
        binding.mainBottomNavigationView.setOnItemSelectedListener(item -> {
            displayFragment(BottomNavigationViewFragment.fromMenuId(item.getItemId()));
            return true;
        });
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
