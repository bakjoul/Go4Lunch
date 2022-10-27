package com.bakjoul.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.FragmentWorkmatesItemBinding;
import com.bumptech.glide.Glide;

public class WorkmatesAdapter extends ListAdapter<WorkmatesItemViewState, WorkmatesAdapter.ViewHolder> {

   protected WorkmatesAdapter() {
      super(new WorkmatesAdapterDiffCallback());
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(FragmentWorkmatesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.bind(getItem(position));
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {

      private final FragmentWorkmatesItemBinding binding;

      public ViewHolder(@NonNull FragmentWorkmatesItemBinding binding) {
         super(binding.getRoot());
         this.binding = binding;
      }

      public void bind(@NonNull WorkmatesItemViewState viewState) {
         ImageView photo = binding.workmatesItemPhoto;
         Glide.with(photo.getContext())
             .load(viewState.getPhotoUrl())
             .circleCrop()
             .into(photo);
         binding.workmatesItemText.setText(viewState.getName());
      }
   }

   private static class WorkmatesAdapterDiffCallback extends DiffUtil.ItemCallback<WorkmatesItemViewState> {

      @Override
      public boolean areItemsTheSame(@NonNull WorkmatesItemViewState oldItem, @NonNull WorkmatesItemViewState newItem) {
         return oldItem.getId().equals(newItem.getId());
      }

      @Override
      public boolean areContentsTheSame(@NonNull WorkmatesItemViewState oldItem, @NonNull WorkmatesItemViewState newItem) {
         return oldItem.equals(newItem);
      }
   }
}
