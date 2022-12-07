package com.bakjoul.go4lunch.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.ActivityMainSuggestionItemBinding;

public class SuggestionsAdapter extends ListAdapter<String, SuggestionsAdapter.ViewHolder> {

    private final OnSuggestionClickListener onSuggestionClickListener;

    public SuggestionsAdapter(OnSuggestionClickListener onSuggestionClickListener) {
        super(new SuggestionsAdapterDiffCallback());
        this.onSuggestionClickListener = onSuggestionClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ActivityMainSuggestionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), this.onSuggestionClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ActivityMainSuggestionItemBinding binding;
        private final OnSuggestionClickListener onSuggestionClickListener;

        public ViewHolder(@NonNull ActivityMainSuggestionItemBinding binding, OnSuggestionClickListener onSuggestionClickListener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);
            this.onSuggestionClickListener = onSuggestionClickListener;
        }

        public void bind(@NonNull String suggestion) {
            itemView.setTag(suggestion);
            binding.mainRecyclerViewItem.setText(suggestion);
        }

        @Override
        public void onClick(View v) {
            this.onSuggestionClickListener.onSuggestionClicked(getAdapterPosition());
        }
    }

    private static class SuggestionsAdapterDiffCallback extends DiffUtil.ItemCallback<String> {

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OnSuggestionClickListener {
        void onSuggestionClicked(int position);
    }
}
