package com.bakjoul.go4lunch.ui.details;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bakjoul.go4lunch.databinding.ActivityDetailsBinding;
import com.bakjoul.go4lunch.ui.utils.ReverseInterpolator;
import com.google.android.material.appbar.AppBarLayout;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.detailsAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isFabDown = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ObjectAnimator slideDownAnimation = ObjectAnimator.ofFloat(binding.detailsFabSelect, "translationY", 0, binding.detailsToolbar.getHeight());
                slideDownAnimation.setDuration(125);

                if (verticalOffset == 0 && isFabDown) {
                    slideDownAnimation.setInterpolator(new ReverseInterpolator());
                    slideDownAnimation.start();
                    isFabDown = false;
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() && !isFabDown) {
                    slideDownAnimation.start();
                    isFabDown = true;
                }
            }
        });

    }
}