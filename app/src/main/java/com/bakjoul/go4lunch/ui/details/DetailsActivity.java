package com.bakjoul.go4lunch.ui.details;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.R;
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

    @NonNull
    public static Intent navigate(Context context, String restaurantId) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("restaurantId", restaurantId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DetailsViewModel viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        DetailsAdapter adapter = new DetailsAdapter();
        binding.detailsRecyclerView.setAdapter(adapter);

        binding.detailsFabBack.setOnClickListener(view -> onBackPressed());

        ImageView photo = binding.detailsRestaurantPhoto;

        viewModel.getDetailsViewStateMediatorLiveData().observe(this, viewState -> {
            if (viewState.isProgressBarGone()) {
                binding.detailsProgressBar.setVisibility(View.GONE);
            }
            if (viewState.isWorkmatesListEmptyStateVisible()) {
                binding.detailsWorkmatesEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.detailsWorkmatesEmpty.setVisibility(View.GONE);
            }

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

            setChooseButton(viewModel, viewState);
            setCallButton(viewState);
            setLikeButton(viewModel, viewState);
            setWebsiteButton(viewState);

            adapter.submitList(viewState.getWorkmatesList());
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

    private void setChooseButton(DetailsViewModel viewModel, @NonNull DetailsViewState viewState) {
        binding.detailsFabSelect.setEnabled(viewState.isProgressBarGone());
        binding.detailsFabSelect.setSelected(viewState.isChosen());
        if (binding.detailsFabSelect.isSelected()) {
            binding.detailsFabSelect.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green));
        } else {
            binding.detailsFabSelect.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));
        }
        binding.detailsFabSelect.setOnClickListener(view -> {
            if (binding.detailsFabSelect.isSelected()) {
                viewModel.onRestaurantUnchoosed();
            } else {
                viewModel.onRestaurantChoosed(viewState.getId(), viewState.getName(), viewState.getAddress());
            }
        });
    }

    private void setCallButton(@NonNull DetailsViewState viewState) {
        if (viewState.isProgressBarGone() && viewState.getPhoneNumber() != null) {
            binding.detailsButtonCall.setEnabled(true);
            binding.detailsButtonCall.setAlpha(1);
            binding.detailsButtonCall.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", viewState.getPhoneNumber(), null));
                DetailsActivity.this.startActivity(intent);
            });
        }
    }

    private void setLikeButton(DetailsViewModel viewModel, @NonNull DetailsViewState viewState) {
        if (viewState.isProgressBarGone()) {
            binding.detailsButtonLike.setEnabled(true);
            binding.detailsButtonLike.setAlpha(1);
            binding.detailsButtonLike.setSelected(viewState.isFavorite());
            binding.detailsButtonLike.setOnClickListener(view -> {
                if (binding.detailsButtonLike.isSelected()) {
                    viewModel.onUnfavoriteButtonClicked(viewState.getId());
                } else {
                    viewModel.onFavoriteButtonClicked(viewState.getId(), viewState.getName());
                }
            });
        }
    }

    private void setWebsiteButton(@NonNull DetailsViewState viewState) {
        if (viewState.isProgressBarGone() && viewState.getWebsiteUrl() != null) {
            binding.detailsButtonWebsite.setEnabled(true);
            binding.detailsButtonWebsite.setAlpha(1);
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
