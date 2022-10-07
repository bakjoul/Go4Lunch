package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.RestaurantMarker;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment2 extends Fragment {

   private static final String TAG = "MapFragment2";

   public static MapFragment2 newInstance() {
      return new MapFragment2();
   }


   @SuppressLint("MissingPermission")
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      Log.d(TAG, "onCreateView: MAPFRAGMENT2");
      MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);

      SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

      if (supportMapFragment != null) {
         supportMapFragment.getMapAsync(googleMap ->
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
                } else {
                   Log.d(TAG, "Location permission is not allowed. Map will not update.");
                }
             })
         );
      }

      return inflater.inflate(R.layout.fragment_map2, container, false);
   }
}
