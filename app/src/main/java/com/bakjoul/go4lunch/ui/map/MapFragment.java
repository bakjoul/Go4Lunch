package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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

        viewModel.getMapViewStateMediatorLiveData().observe(getViewLifecycleOwner(), viewState -> {
                if (viewState != null) {
                    MapFragment.this.getMapAsync(googleMap -> {
                        googleMap.clear();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                viewState.getLatLng(),
                                13.25f
                            )
                        );
                        googleMap.setMyLocationEnabled(true);

                        // Custom location button position
                        View locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        rlp.setMargins(0, 0, 30, 30);

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
                    });
                } else {
                    Log.d(TAG, "Location permission is not allowed. Map will not update.");
                }
            }
        );
    }
}
