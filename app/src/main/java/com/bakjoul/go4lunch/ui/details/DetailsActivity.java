package com.bakjoul.go4lunch.ui.details;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.databinding.ActivityDetailsBinding;
import com.bakjoul.go4lunch.ui.utils.ReverseInterpolator;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;

    public static void navigate(String restaurantId, Activity sourceActivity) {
        Bundle arg = new Bundle();
        arg.putString("restaurantId", restaurantId);
        Intent intent = new Intent(sourceActivity, DetailsActivity.class);
        intent.putExtras(arg);
        sourceActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DetailsViewModel viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        ImageView photo = binding.detailsRestaurantPhoto;

        viewModel.getDetailsViewState().observe(this, viewState -> {
            Glide.with(photo.getContext())
                .load(viewState.getPhotoUrl())
                .centerCrop()
                .into(photo);
            binding.detailsRestaurantName.setText(viewState.getName());
            binding.detailsRestaurantRating.setRating(viewState.getRating());
            if (viewState.isRatingBarVisible()) {
                binding.detailsRestaurantRating.setVisibility(View.VISIBLE);
            } else {
                binding.detailsRestaurantRating.setVisibility(View.GONE);
            }
            binding.detailsRestaurantAddress.setText(viewState.getAddress());
            binding.detailsRestaurantOpeningStatus.setText(viewState.getOpeningStatus());
        });

        binding.detailsAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isFabDown = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ObjectAnimator animation = getObjectAnimator();

                // When expanded
                if (verticalOffset == 0 && isFabDown) {
                    animation.setInterpolator(new ReverseInterpolator());
                    animation.start();  // FAB will anchor to toolbar top end
                    isFabDown = false;
                // When collapsed
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() && !isFabDown) {
                    animation.start();  // FAB will anchor to toolbar bottom end
                    isFabDown = true;
                }
            }
        });

    }

    @NonNull
    private ObjectAnimator getObjectAnimator() {
        // Translate animation
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", 0, binding.detailsToolbar.getHeight());
        // Alpha animation
        PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 1, 0.75f);
        // Animation object
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(binding.detailsFabSelect, pvhY, pvhAlpha);
        animation.setDuration(250);
        return animation;
    }
}