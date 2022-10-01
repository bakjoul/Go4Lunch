package com.bakjoul.go4lunch.ui.details;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bakjoul.go4lunch.databinding.ActivityDetailsBinding;
import com.bakjoul.go4lunch.ui.utils.ReverseInterpolator;
import com.google.android.material.appbar.AppBarLayout;

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

        binding.detailsAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isFabDown = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Translate animation
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", 0, binding.detailsToolbar.getHeight());
                // Alpha animation
                PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 1, 0.75f);
                // Animation object
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(binding.detailsFabSelect, pvhY, pvhAlpha);
                animation.setDuration(250);

                // Collapsed
                if (verticalOffset == 0 && isFabDown) {
                    animation.setInterpolator(new ReverseInterpolator());
                    animation.start();
                    isFabDown = false;
                    // Expanded
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() && !isFabDown) {
                    animation.start();
                    isFabDown = true;
                }
            }
        });

    }
}