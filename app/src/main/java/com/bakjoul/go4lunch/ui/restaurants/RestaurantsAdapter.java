package com.bakjoul.go4lunch.ui.restaurants;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.FragmentRestaurantsItemBinding;

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

        public void bind(@NonNull RestaurantsItemViewState restaurantsItemViewState) {
            binding.restaurantsItemName.setText(restaurantsItemViewState.getName());
            binding.restaurantsItemLocation.setText(restaurantsItemViewState.getLocation());
            binding.restaurantsItemIsOpen.setText(restaurantsItemViewState.getIsOpen());
            binding.restaurantsItemDistance.setText(restaurantsItemViewState.getDistance());
            binding.restaurantsItemAttendance.setText(restaurantsItemViewState.getAttendance());
            binding.restaurantsItemRating.setRating(restaurantsItemViewState.getRating());
            // TODO Photo
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
