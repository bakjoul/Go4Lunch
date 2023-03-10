package com.bakjoul.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.ActivityChatItemReceiverBinding;

public class ChatAdapterSimple extends ListAdapter<ChatMessageItemViewState, ChatAdapterSimple.ViewHolder> {

    public ChatAdapterSimple() {
        super(new ChatAdapterDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ActivityChatItemReceiverBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ActivityChatItemReceiverBinding binding;

        public ViewHolder(@NonNull ActivityChatItemReceiverBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull ChatMessageItemViewState viewState) {
            binding.chatItemReceiverMessage.setText(viewState.getContent());
        }
    }

    private static class ChatAdapterDiffCallback extends DiffUtil.ItemCallback<ChatMessageItemViewState> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessageItemViewState oldItem, @NonNull ChatMessageItemViewState newItem) {
            return oldItem.getTimestamp().equals(newItem.getTimestamp());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessageItemViewState oldItem, @NonNull ChatMessageItemViewState newItem) {
            return oldItem.equals(newItem);
        }
    }


}
