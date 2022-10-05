package com.bakjoul.go4lunch.ui.details;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.databinding.ActivityDetailsBinding;
import com.bakjoul.go4lunch.ui.utils.DensityUtil;
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

      binding.detailsFabBack.setOnClickListener(view -> onBackPressed());

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
         boolean isInfoPaddingChanged = false;

         @Override
         public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            ObjectAnimator fabAnimation = getFabAnimator();
            ValueAnimator infoPaddingAnimation = getInfoPaddingAnimator();

            // When expanded, fab will go up
            if (verticalOffset == 0 && isFabDown) {
               fabAnimation.setInterpolator(new ReverseInterpolator());
               fabAnimation.start();
               isFabDown = false;
            }
            // When collapsed, fab will go down
            else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() && !isFabDown) {
               fabAnimation.start();
               isFabDown = true;
            }

            // When collapsing, info padding will be increased
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() - binding.detailsFabBack.getHeight() && isInfoPaddingChanged) {
               infoPaddingAnimation.start();
               isInfoPaddingChanged = false;
            }
            // When expanding, info padding will be decreased
            else if (Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange() - binding.detailsFabBack.getHeight() && !isInfoPaddingChanged) {
               infoPaddingAnimation.setInterpolator(new ReverseInterpolator());
               infoPaddingAnimation.start();
               isInfoPaddingChanged = true;
            }
         }
      });

   }

   @NonNull
   private ObjectAnimator getFabAnimator() {
      // Translate animation
      PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", 0, binding.detailsToolbar.getHeight());
      // Alpha animation
      PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 1, 0.75f);
      return ObjectAnimator.ofPropertyValuesHolder(binding.detailsFabSelect, pvhY, pvhAlpha);
   }

   @NonNull
   private ValueAnimator getInfoPaddingAnimator() {
      ValueAnimator infoPaddingAnimation = ValueAnimator.ofInt(DensityUtil.dip2px(getApplicationContext(), 10), DensityUtil.dip2px(getApplicationContext(), 48));
      infoPaddingAnimation.addUpdateListener(valueAnimator ->
          binding.detailsRestaurantInfo.setPadding((Integer) valueAnimator.getAnimatedValue(), 0, 0, 0));
      return infoPaddingAnimation;
   }
}