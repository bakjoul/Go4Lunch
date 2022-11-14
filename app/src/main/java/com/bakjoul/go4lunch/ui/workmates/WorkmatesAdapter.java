package com.bakjoul.go4lunch.ui.workmates;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.FragmentWorkmatesItemBinding;
import com.bumptech.glide.Glide;

public class WorkmatesAdapter extends ListAdapter<WorkmateItemViewState, WorkmatesAdapter.ViewHolder> {

    private final OnWorkmateClickListener onWorkmateClickListener;

    public WorkmatesAdapter(OnWorkmateClickListener onWorkmateClickListener) {
        super(new WorkmatesAdapterDiffCallback());
        this.onWorkmateClickListener = onWorkmateClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentWorkmatesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), this.onWorkmateClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final FragmentWorkmatesItemBinding binding;
        private final OnWorkmateClickListener onWorkmateClickListener;

        public ViewHolder(@NonNull FragmentWorkmatesItemBinding binding, OnWorkmateClickListener onWorkmateClickListener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);
            this.onWorkmateClickListener = onWorkmateClickListener;
        }

        public void bind(@NonNull WorkmateItemViewState viewState) {
            itemView.setTag(viewState.getChosenRestaurantId());
            ImageView photo = binding.workmatesItemPhoto;
            Glide.with(photo.getContext())
                .load(viewState.getPhotoUrl())
                .circleCrop()
                .into(photo);
            if (viewState.getChosenRestaurantName() == null) {
                binding.workmatesItemText.setText(String.format("%s n'a pas encore décidé", viewState.getName()));
                binding.workmatesItemText.setTypeface(null, Typeface.ITALIC);
                binding.workmatesItemText.setTextColor(itemView.getResources().getColor(R.color.grey));
            } else {
                binding.workmatesItemText.setText(String.format("%s déjeune à %s", viewState.getName(), viewState.getChosenRestaurantName()));
                binding.workmatesItemText.setTypeface(null, Typeface.BOLD);
                binding.workmatesItemText.setTextColor(itemView.getResources().getColor(R.color.black));
            }
        }

        @Override
        public void onClick(View v) {
            this.onWorkmateClickListener.onWorkmateClicked(getAdapterPosition());
        }
    }

    private static class WorkmatesAdapterDiffCallback extends DiffUtil.ItemCallback<WorkmateItemViewState> {

        @Override
        public boolean areItemsTheSame(@NonNull WorkmateItemViewState oldItem, @NonNull WorkmateItemViewState newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull WorkmateItemViewState oldItem, @NonNull WorkmateItemViewState newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OnWorkmateClickListener {
        void onWorkmateClicked(int position);
    }
}
