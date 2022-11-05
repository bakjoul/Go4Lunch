package com.bakjoul.go4lunch.ui.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.ActivityDetailsItemBinding;
import com.bumptech.glide.Glide;

public class DetailsAdapter extends ListAdapter<DetailsItemViewState, DetailsAdapter.ViewHolder> {

    public DetailsAdapter() {
        super(new DetailsAdapterDiffCallback());
    }

    @NonNull
    @Override
    public DetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ActivityDetailsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsAdapter.ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ActivityDetailsItemBinding binding;

        public ViewHolder(@NonNull ActivityDetailsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull DetailsItemViewState viewState) {
            ImageView photo = binding.detailsItemPhoto;
            Glide.with(photo.getContext())
                .load(viewState.getUserPhotoUrl())
                .circleCrop()
                .into(photo);
            binding.detailsItemText.setText(viewState.getMessage());
        }
    }

    private static class DetailsAdapterDiffCallback extends DiffUtil.ItemCallback<DetailsItemViewState> {

        @Override
        public boolean areItemsTheSame(@NonNull DetailsItemViewState oldItem, @NonNull DetailsItemViewState newItem) {
            return oldItem.getUserId().equals(newItem.getUserId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DetailsItemViewState oldItem, @NonNull DetailsItemViewState newItem) {
            return oldItem.equals(newItem);
        }
    }
}
