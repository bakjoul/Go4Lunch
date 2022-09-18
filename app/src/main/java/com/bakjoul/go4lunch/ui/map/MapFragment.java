package com.bakjoul.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

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

        viewModel.getMapViewStateLiveData().observe(getViewLifecycleOwner(), mapViewState -> {
                if (mapViewState != null) {
                    MapFragment.this.getMapAsync(googleMap -> {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(
                                    mapViewState.getLatitude(),
                                    mapViewState.getLongitude()
                                ),
                            13.5F
                            )
                        );
                        googleMap.setMyLocationEnabled(true);

                        // Custom location button position
                        View locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        rlp.setMargins(0,0,30,30);
                    });
                } else {
                    Log.d(TAG, "Location permission is not allowed. Map will not update.");
                }
            }
        );
    }
}
