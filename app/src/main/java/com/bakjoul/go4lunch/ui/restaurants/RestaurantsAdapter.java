package com.bakjoul.go4lunch.ui.restaurants;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.FragmentRestaurantsItemBinding;
import com.bumptech.glide.Glide;

public class RestaurantsAdapter extends ListAdapter<RestaurantsItemViewState, RestaurantsAdapter.ViewHolder> {

    public RestaurantsAdapter() {
        super(new RestaurantsAdapterDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentRestaurantsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final FragmentRestaurantsItemBinding binding;

        public ViewHolder(@NonNull FragmentRestaurantsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull RestaurantsItemViewState viewState) {
            binding.restaurantsItemName.setText(viewState.getName());
            binding.restaurantsItemLocation.setText(viewState.getLocation());
            binding.restaurantsItemIsOpen.setText(viewState.getIsOpen());
            binding.restaurantsItemDistance.setText(viewState.getDistance());
            binding.restaurantsItemAttendance.setText(viewState.getAttendance());
            binding.restaurantsItemRating.setRating(viewState.getRating());

            ImageView photo = binding.restaurantsItemPhoto;
            Glide.with(photo.getContext())
                .load(viewState.getPhotoUrl())
                .centerCrop()
                .into(photo);
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
}
