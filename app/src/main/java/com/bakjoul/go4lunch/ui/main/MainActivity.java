package com.bakjoul.go4lunch.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivityMainBinding;
import com.bakjoul.go4lunch.ui.list.ListFragment;
import com.bakjoul.go4lunch.ui.map.MapFragment;
import com.bakjoul.go4lunch.ui.workmates.WorkmatesFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private MapFragment mapFragment = null;
    private ListFragment listFragment = null;
    private WorkmatesFragment workmatesFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setBottomNavigationView();

        if (savedInstanceState == null) {
            displayFragment(0);
        }
    }

    private void displayFragment(int id) {
        Fragment fragment;
        switch (id) {
            case 0:
                fragment = new MapFragment();
                break;
            case 1:
                fragment = new ListFragment();
                break;
            case 2:
                fragment = new WorkmatesFragment();
                break;
            default:
                throw new IllegalStateException("Unknown id :" + id);
        }

        getSupportFragmentManager().beginTransaction()
            .replace(binding.mainFrameLayoutFragmentContainer.getId(), fragment)
            .commit();
    }

    private void setBottomNavigationView() {
        binding.mainBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottomNavigationView_menu_mapView:
                        displayFragment(0);
                        break;
                    case R.id.bottomNavigationView_menu_listView:
                        displayFragment(1);
                        break;
                    case R.id.bottomNavigationView_menu_workmates:
                        displayFragment(2);
                        break;
                }
                return true;
            }
        });
    }


/*
        }*/


}