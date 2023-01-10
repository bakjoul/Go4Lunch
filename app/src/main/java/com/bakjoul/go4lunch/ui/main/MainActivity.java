package com.bakjoul.go4lunch.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
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
public class MainActivity extends AppCompatActivity implements SuggestionsAdapter.OnSuggestionClickListener {

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
        setSearchView();
        SuggestionsAdapter adapter = new SuggestionsAdapter(this);
        binding.mainSuggestionsRecyclerView.setAdapter(adapter);
        setBottomNavigationView();
        DrawerLayout drawerLayout = binding.mainDrawerLayout;
        drawerLayout.setStatusBarBackground(R.color.primaryDarkColor);

        View header = binding.mainNavigationView.getHeaderView(0);
        AppCompatImageView photo = header.findViewById(R.id.main_drawer_user_photo);
        TextView username = header.findViewById(R.id.main_drawer_user_name);
        TextView email = header.findViewById(R.id.main_drawer_user_email);

        viewModel.getMainViewStateMediatorLiveData().observe(this, viewState -> {
            Glide.with(photo.getContext())
                .load(viewState.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(photo);
            username.setText(viewState.getUsername());
            email.setText(viewState.getEmail());

            setMainNavigationView(viewState);
            if (viewState.getSuggestions().isEmpty()) {
                binding.mainSuggestionsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.mainSuggestionsRecyclerView.setVisibility(View.VISIBLE);
            }
            adapter.submitList(viewState.getSuggestions());
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
        toolbar.setNavigationOnClickListener(view ->
            binding.mainDrawerLayout.openDrawer(GravityCompat.START)
        );
    }

    private void setSearchView() {
        SearchView searchView = binding.mainSearchView;
        ImageView searchClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchClose.setColorFilter(R.color.grey, PorterDuff.Mode.SRC_ATOP);
        searchClose.setEnabled(true);

        searchView.setOnSearchClickListener(v -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });
        searchView.setOnCloseListener(() -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            return false;
        });
        // Closes search view if it loses focus
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchView.setIconified(true);
                searchView.onActionViewCollapsed();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.onSearchViewQueryTextChanged(newText);
                if (newText.isEmpty()) {
                    viewModel.onSuggestionClicked(null);
                }
                return false;
            }
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
            if (fragment instanceof MapFragment) {
                if (selected == FragmentToDisplay.MAP) {
                    setToolbarTitleAndSearchview(selected);
                    transaction.attach(fragment);
                    shown = true;
                } else {
                    transaction.detach(fragment);
                }
            } else if (fragment instanceof RestaurantsFragment) {
                if (selected == FragmentToDisplay.RESTAURANTS) {
                    setToolbarTitleAndSearchview(selected);
                    transaction.attach(fragment);
                    shown = true;
                } else {
                    transaction.detach(fragment);
                }
            } else if (fragment instanceof WorkmatesFragment) {
                if (selected == FragmentToDisplay.WORKMATES) {
                    setToolbarTitleAndSearchview(selected);
                    transaction.attach(fragment);
                    shown = true;
                } else {
                    transaction.detach(fragment);
                }
            } else if (fragment instanceof NoPermissionFragment) {
                if (selected == FragmentToDisplay.NO_PERMISSION) {
                    transaction.attach(fragment);
                    shown = true;
                } else {
                    transaction.detach(fragment);
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
                    binding.mainSearchView.setQueryHint(getString(R.string.main_searchview_query_hint_restaurants));
                    transaction.add(binding.mainFrameLayoutFragmentContainer.getId(), RestaurantsFragment.newInstance());
                    break;
                case WORKMATES:
                    binding.mainToolbar.setTitle(R.string.toolbar_title_workmates);
                    binding.mainSearchView.setQueryHint(getString(R.string.main_searchview_query_hint_workmates));
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

    private void setToolbarTitleAndSearchview(@NonNull FragmentToDisplay selected) {
        switch (selected) {
            case MAP:
            case RESTAURANTS:
                if (binding.mainToolbar.getTitle() != getString(R.string.toolbar_title_hungry)) {
                    binding.mainToolbar.setTitle(R.string.toolbar_title_hungry);
                }
                if (binding.mainSearchView.getQueryHint() != getString(R.string.main_searchview_query_hint_restaurants)) {
                    binding.mainSearchView.setQueryHint(getString(R.string.main_searchview_query_hint_restaurants));
                }
                break;
            case WORKMATES:
                if (binding.mainToolbar.getTitle() != getString(R.string.toolbar_title_workmates)) {
                    binding.mainToolbar.setTitle(R.string.toolbar_title_workmates);
                }
                if (binding.mainSearchView.getQueryHint() != getString(R.string.main_searchview_query_hint_workmates)) {
                    binding.mainSearchView.setQueryHint(getString(R.string.main_searchview_query_hint_workmates));
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

    @Override
    public void onSuggestionClicked(int position) {
        if (binding.mainSuggestionsRecyclerView.getLayoutManager() != null) {
            binding.mainSearchView.setQuery(binding.mainSuggestionsRecyclerView.getLayoutManager().findViewByPosition(position).getTag().toString(), true);
            viewModel.onSuggestionClicked(binding.mainSuggestionsRecyclerView.getLayoutManager().findViewByPosition(position).getTag().toString());
        }
    }
}
