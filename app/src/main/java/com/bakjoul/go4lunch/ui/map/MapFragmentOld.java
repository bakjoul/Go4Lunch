package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.bakjoul.go4lunch.ui.utils.SvgToBitmap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragmentOld extends SupportMapFragment {

   private static final String TAG = "MapFragment";

   @NonNull
   public static MapFragmentOld newInstance() {
      return new MapFragmentOld();
   }

   @SuppressLint("MissingPermission")
   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

      MapFragmentOld.this.getMapAsync(googleMap ->
          viewModel.getMapViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
             if (viewState != null) {
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
                          .icon(getIcon(m));
                      googleMap
                          .addMarker(marker)
                          .setTag(m.getId());
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

   @NonNull
   private BitmapDescriptor getIcon(@NonNull RestaurantMarker m) {
      return BitmapDescriptorFactory.fromBitmap(new SvgToBitmap().getBitmapFromVectorDrawable(getContext(), m.getIcon()));
   }
}
