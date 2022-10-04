package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends SupportMapFragment {

   private static final String TAG = "MapFragment";

   @NonNull
   public static MapFragment newInstance() {
      return new MapFragment();
   }

   @SuppressLint("MissingPermission")
   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

      MapFragment.this.getMapAsync(googleMap ->
          viewModel.getMapViewStateMediatorLiveData().observe(getViewLifecycleOwner(), viewState -> {
             if (viewState != null) {
                googleMap.clear();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        viewState.getLatLng(),
                        13.5f
                    )
                );
                googleMap.setMyLocationEnabled(true);

                if (!viewState.getRestaurantsMarkers().isEmpty()) {
                   for (MarkerOptions m : viewState.getRestaurantsMarkers().keySet()) {
                      googleMap
                          .addMarker(m)
                          .setTag(viewState.getRestaurantsMarkers().get(m));
                   }

                   googleMap.setOnMarkerClickListener(marker -> {
                      DetailsActivity.navigate((String) marker.getTag(), getActivity());
                      return true;
                   });
                }
             } else {
                Log.d(TAG, "Location permission is not allowed. Map will not update.");
             }
          })
      );
   }
}
