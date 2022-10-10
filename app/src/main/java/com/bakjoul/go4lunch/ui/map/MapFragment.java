package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.ErrorType;
import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.databinding.FragmentMapBinding;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private GoogleMap googleMap;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(googleMap -> {
                this.googleMap = googleMap;
                viewModel.onMapReady();
            });
        }

        viewModel.getMapViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
            if (!viewState.isProgressBarVisible()) {
                binding.mapProgressBar.setVisibility(View.GONE);
            } else {
                binding.mapProgressBar.setVisibility(View.VISIBLE);
            }
            googleMap.clear();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    viewState.getLatLng(),
                    13.5f
                )
            );
            googleMap.setMyLocationEnabled(true);

            if (!viewState.getRestaurantsMarkers().isEmpty()) {
                for (RestaurantMarker m : viewState.getRestaurantsMarkers()) {
                    MarkerOptions marker = new MarkerOptions()
                        .position(m.getPosition())
                        .title(m.getTitle())
                        .icon(m.getIcon());
                    googleMap
                        .addMarker(marker)
                        .setTag(m.getId());
                }

                googleMap.setOnMarkerClickListener(marker -> {
                    DetailsActivity.navigate((String) marker.getTag(), getActivity());
                    return true;
                });
            }
        });

        viewModel.getErrorTypeSingleLiveEvent().observe(getViewLifecycleOwner(), errorType -> {
            if (errorType == ErrorType.TIMEOUT) {
                Snackbar
                    .make(binding.getRoot(), R.string.snackbar_timeout, Snackbar.LENGTH_INDEFINITE)
                    .setAnchorView(binding.mapProgressBar)
                    .setAction(R.string.snackbar_retry, view -> viewModel.onRetryButtonClicked())
                    .show();
            }
        });

        return binding.getRoot();
    }
}
