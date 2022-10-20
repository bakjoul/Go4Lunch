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
import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.databinding.FragmentMapBinding;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.bakjoul.go4lunch.ui.utils.SvgToBitmap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {

   public static final float MOVE_CAMERA_ZOOM = 13.5f;

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

      final Snackbar retryBar = Snackbar
          .make(binding.getRoot(), R.string.snackbar_message, Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.snackbar_retry, view -> viewModel.onRetryButtonClicked());

      if (supportMapFragment != null) {
         // On map ready
         supportMapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            viewModel.onMapReady();
            googleMap.setMinZoomPreference(12);
            googleMap.setMaxZoomPreference(16);
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(() -> {
               viewModel.onMyLocationButtonClicked();
               return false;
            });
            // Handles camera moves
            googleMap.setOnCameraIdleListener(() -> viewModel.onCameraMoved(googleMap.getCameraPosition().target));
            googleMap.setOnCameraMoveStartedListener(reason -> {
               if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                  viewModel.onCameraMovedByUser();
                  if (retryBar.isShown()) {
                     retryBar.dismiss();
                  }
               }
            });

            // Updates camera according to location
            viewModel.getCameraSingleLiveEvent().observe(getViewLifecycleOwner(), location ->
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, MOVE_CAMERA_ZOOM)));

            viewModel.getIsRetryBarVisible().observe(getViewLifecycleOwner(), isRetryBarVisible -> {
               if (isRetryBarVisible) {
                  retryBar.show();
               }
            });
         });
      }

      viewModel.getMapViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
         googleMap.clear();

         // Progress bar visibility
         if (!viewState.isProgressBarVisible()) {
            binding.mapProgressBar.setVisibility(View.GONE);
         } else {
            binding.mapProgressBar.setVisibility(View.VISIBLE);
         }

         // Adds markers
         for (RestaurantMarker marker : viewState.getRestaurantsMarkers()) {
            MarkerOptions markerOptions = new MarkerOptions()
                .position(marker.getPosition())
                .title(marker.getTitle())
                .icon(getIcon(marker));
            googleMap
                .addMarker(markerOptions)
                .setTag(marker.getId());
         }

         // Adds markers click listeners
         googleMap.setOnMarkerClickListener(marker -> {
            DetailsActivity.navigate((String) marker.getTag(), getActivity());
            return true;
         });
      });

      return binding.getRoot();
   }

   @NonNull
   private BitmapDescriptor getIcon(@NonNull RestaurantMarker m) {
      return BitmapDescriptorFactory.fromBitmap(new SvgToBitmap().getBitmapFromVectorDrawable(getContext(), m.getIcon()));
   }
}
