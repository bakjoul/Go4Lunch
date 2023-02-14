package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.restaurants.model.RestaurantMarker;
import com.bakjoul.go4lunch.databinding.FragmentMapBinding;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.bakjoul.go4lunch.ui.utils.SvgToBitmap;
import com.bakjoul.go4lunch.ui.utils.markers_overlay.FloatingMarkerTitlesOverlay;
import com.bakjoul.go4lunch.ui.utils.markers_overlay.MarkerInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    private static final float MIN_ZOOM_PREFERENCE = 12;
    private static final float MAX_ZOOM_PREFERENCE = 20;
    private static final float MOVE_CAMERA_ZOOM = 13.5f;

    private FragmentMapBinding binding;
    private FloatingMarkerTitlesOverlay floatingMarkerOverlay;

    @NonNull
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        final Snackbar retryBar = Snackbar
            .make(binding.getRoot(), R.string.snackbar_message, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.snackbar_retry, view -> viewModel.onRetryButtonClicked());

        if (supportMapFragment != null) {
            // On map ready
            supportMapFragment.getMapAsync(googleMap -> {
                setFloatingMarkersOverlay(googleMap);
                viewModel.onMapReady();
                googleMap.setMinZoomPreference(MIN_ZOOM_PREFERENCE);
                googleMap.setMaxZoomPreference(MAX_ZOOM_PREFERENCE);
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMyLocationButtonClickListener(() -> {
                    viewModel.onMyLocationButtonClicked();
                    return false;
                });
                // Handles camera moves
                googleMap.setOnCameraIdleListener(() -> viewModel.onCameraMoved(googleMap.getCameraPosition().target));
                googleMap.setOnCameraMoveStartedListener(reason -> {
                    if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                        retryBar.dismiss();
                        viewModel.onCameraMovedByUser();
                    }
                });

                // Updates camera according to location
                viewModel.getCameraSingleLiveEvent().observe(getViewLifecycleOwner(), location ->
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, MOVE_CAMERA_ZOOM))
                );

                viewModel.getIsRetryBarVisibleSingleLiveEvent().observe(getViewLifecycleOwner(), isRetryBarVisible -> {
                    if (isRetryBarVisible) {
                        retryBar.show();
                    }
                });

                viewModel.getMapViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
                    // Progress bar visibility
                    if (!viewState.isProgressBarVisible()) {
                        binding.mapProgressBar.setVisibility(View.GONE);
                    } else {
                        binding.mapProgressBar.setVisibility(View.VISIBLE);
                    }

                    googleMap.clear();
                    floatingMarkerOverlay.clearMarkers();

                    // Adds markers
                    List<RestaurantMarker> markersList = viewState.getRestaurantsMarkers();
                    for (int i = 0; i < markersList.size(); i++) {
                        MarkerOptions markerOptions = new MarkerOptions()
                            .position(markersList.get(i).getPosition())
                            .title(markersList.get(i).getTitle())
                            .icon(getIcon(markersList.get(i)));
                        googleMap
                            .addMarker(markerOptions)
                            .setTag(markersList.get(i).getId());

                        // Adds markers title overlay
                        MarkerInfo markerInfo = new MarkerInfo(markersList.get(i).getPosition(), markersList.get(i).getTitle(), getResources().getColor(R.color.white));
                        floatingMarkerOverlay.addMarker(i, markerInfo);
                    }

                    // Adds markers click listeners
                    googleMap.setOnMarkerClickListener(marker -> {
                        DetailsActivity.navigate((String) marker.getTag(), getActivity());
                        return true;
                    });

                });
            });
        }

        viewModel.getIsUserSearchUnmatchedSingleLiveEvent().observe(getViewLifecycleOwner(), isSearchUnmatched -> {
            if (isSearchUnmatched) {
                Toast.makeText(requireContext(), R.string.no_match, Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    @NonNull
    private BitmapDescriptor getIcon(@NonNull RestaurantMarker m) {
        return BitmapDescriptorFactory.fromBitmap(new SvgToBitmap().getBitmapFromVectorDrawable(getContext(), m.getIcon()));
    }

    private void setFloatingMarkersOverlay(GoogleMap googleMap) {
        floatingMarkerOverlay = binding.mapOverlay;
        floatingMarkerOverlay.setSource(googleMap);
        floatingMarkerOverlay.setTextSizeDIP(12);
        floatingMarkerOverlay.setMaxTextWidthDIP(128);
        floatingMarkerOverlay.setMaxTextHeightDIP(32);
    }
}
