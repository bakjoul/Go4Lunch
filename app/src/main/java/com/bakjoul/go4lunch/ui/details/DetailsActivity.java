package com.bakjoul.go4lunch.ui.details;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

   private static final long ANIMATION_DURATION = 250;

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

         setCallButton(viewState);
         setWebsiteButton(viewState);
      });

      binding.detailsAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
         boolean isFabDown = false;
         boolean isInfoAlignedLeft = true;

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
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() - binding.detailsFabBack.getHeight() && isInfoAlignedLeft) {
               infoPaddingAnimation.start();
               isInfoAlignedLeft = false;
            }
            // When expanding, info padding will be decreased
            else if (Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange() - binding.detailsFabBack.getHeight() && !isInfoAlignedLeft) {
               infoPaddingAnimation.setInterpolator(new ReverseInterpolator());
               infoPaddingAnimation.start();
               isInfoAlignedLeft = true;
            }
         }
      });
   }

   private void setCallButton(@NonNull DetailsViewState viewState) {
      if (viewState.getPhoneNumber() == null) {
         binding.detailsButtonCall.setEnabled(false);
         binding.detailsButtonCall.setAlpha(0.25f);
      } else {
         binding.detailsButtonCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", viewState.getPhoneNumber(), null));
            DetailsActivity.this.startActivity(intent);
         });
      }
   }

   private void setWebsiteButton(@NonNull DetailsViewState viewState) {
      if (viewState.getWebsiteUrl() == null) {
         binding.detailsButtonWebsite.setEnabled(false);
         binding.detailsButtonWebsite.setAlpha(0.25f);
      } else {
         binding.detailsButtonWebsite.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewState.getWebsiteUrl()));
            startActivity(intent);
         });
      }
   }

   @NonNull
   private ObjectAnimator getFabAnimator() {
      // Translate animation
      PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("translationY", 0, binding.detailsToolbar.getHeight());
      // Alpha animation
      PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 1, 0.75f);
      ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.detailsFabSelect, pvhY, pvhAlpha);
      objectAnimator.setDuration(ANIMATION_DURATION);
      return objectAnimator;
   }

   @NonNull
   private ValueAnimator getInfoPaddingAnimator() {
      ValueAnimator valueAnimator = ValueAnimator.ofInt(DensityUtil.dip2px(getApplicationContext(), 10), DensityUtil.dip2px(getApplicationContext(), 44));
      valueAnimator.setDuration(ANIMATION_DURATION);
      valueAnimator.addUpdateListener(animator ->
          binding.detailsRestaurantInfo.setPadding((Integer) animator.getAnimatedValue(), 0, 0, 0));
      return valueAnimator;
   }
}
