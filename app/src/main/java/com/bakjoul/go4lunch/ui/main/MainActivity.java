package com.bakjoul.go4lunch.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivityMainBinding;
import com.bakjoul.go4lunch.ui.list.ListFragment;
import com.bakjoul.go4lunch.ui.map.MapFragment;
import com.bakjoul.go4lunch.ui.workmates.WorkmatesFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

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
        String tag;
        boolean found = true;

        switch (id) {
            case 0:
                tag = "MapFragment";
                MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(tag);
                if (mapFragment == null) {
                    found = false;
                    mapFragment = MapFragment.newInstance();
                }
                fragment = mapFragment;
                break;
            case 1:
                tag = "ListFragment";
                ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(tag);
                if (listFragment == null) {
                    found = false;
                    listFragment = ListFragment.newInstance();
                }
                fragment = listFragment;
                break;
            case 2:
                tag = "WormatesFragment";
                WorkmatesFragment workmatesFragment = (WorkmatesFragment) getSupportFragmentManager().findFragmentByTag(tag);
                if (workmatesFragment == null) {
                    found = false;
                    workmatesFragment = WorkmatesFragment.newInstance();
                }
                fragment = workmatesFragment;
                break;
            default:
                throw new IllegalStateException("Unknown id :" + id);
        }

        Fragment previousFragment = getSupportFragmentManager().findFragmentById(binding.mainFrameLayoutFragmentContainer.getId());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (previousFragment != null) {
            transaction.detach(previousFragment);
        }

        if (found) {
            transaction.attach(fragment);
        } else {
            transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), fragment, tag);
        }

        transaction.commit();
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