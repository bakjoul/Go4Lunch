package com.bakjoul.go4lunch.ui.restaurants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.FragmentRestaurantsItemBinding;
import com.bumptech.glide.Glide;

public class RestaurantsAdapter extends ListAdapter<RestaurantsItemViewState, RestaurantsAdapter.ViewHolder> {

   private final OnRestaurantClickListener onRestaurantClickListener;

   public RestaurantsAdapter(OnRestaurantClickListener onRestaurantClickListener) {
      super(new RestaurantsAdapterDiffCallback());
      this.onRestaurantClickListener = onRestaurantClickListener;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(FragmentRestaurantsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), this.onRestaurantClickListener);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.bind(getItem(position));
   }

   public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

      private final FragmentRestaurantsItemBinding binding;
      private final OnRestaurantClickListener onRestaurantClickListener;

      public ViewHolder(@NonNull FragmentRestaurantsItemBinding binding, OnRestaurantClickListener onRestaurantClickListener) {
         super(binding.getRoot());
         this.binding = binding;

         binding.getRoot().setOnClickListener(this);
         this.onRestaurantClickListener = onRestaurantClickListener;
      }

      public void bind(@NonNull RestaurantsItemViewState viewState) {
         itemView.setTag(viewState.getId());
         binding.restaurantsItemName.setText(viewState.getName());
         binding.restaurantsItemLocation.setText(viewState.getAddress());
         binding.restaurantsItemIsOpen.setText(viewState.getIsOpen());
         binding.restaurantsItemDistance.setText(viewState.getDistance());
         binding.restaurantsItemAttendance.setText(viewState.getAttendance());
         binding.restaurantsItemRating.setRating(viewState.getRating());
         if (viewState.isRatingBarVisible()) {
            binding.restaurantsItemRating.setVisibility(View.VISIBLE);
         } else {
            binding.restaurantsItemRating.setVisibility(View.GONE);
         }
         ImageView photo = binding.restaurantsItemPhoto;
         Glide.with(photo.getContext())
             .load(viewState.getPhotoUrl())
             .centerCrop()
             .into(photo);
      }

      @Override
      public void onClick(View view) {
         this.onRestaurantClickListener.OnRestaurantClicked(getAdapterPosition());
      }
   }

   private static class RestaurantsAdapterDiffCallback extends DiffUtil.ItemCallback<RestaurantsItemViewState> {

      @Override
      public boolean areItemsTheSame(@NonNull RestaurantsItemViewState oldItem, @NonNull RestaurantsItemViewState newItem) {
         return oldItem.getId().equals(newItem.getId());
      }

      @Override
      public boolean areContentsTheSame(@NonNull RestaurantsItemViewState oldItem, @NonNull RestaurantsItemViewState newItem) {
         return oldItem.equals(newItem);
      }
   }

   public interface OnRestaurantClickListener {
      void OnRestaurantClicked(int position);
   }
}
