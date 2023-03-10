package com.bakjoul.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.databinding.ActivityChatItemReceiverBinding;
import com.bakjoul.go4lunch.databinding.ActivityChatItemSenderBinding;

public class ChatAdapter extends ListAdapter<ChatMessageItemViewState, RecyclerView.ViewHolder> {

    public ChatAdapter() {
        super(new ChatAdapterDiffCallback());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ReceiverViewHolder(ActivityChatItemReceiverBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case 1:
                return new SenderViewHolder(ActivityChatItemSenderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((ReceiverViewHolder) holder).bind(getItem(position));
                break;
            case 1:
                ((SenderViewHolder) holder).bind(getItem(position));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + holder.getItemViewType());
        }
    }


    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(ChatMessageItemViewState viewState);
    }

    public static class ReceiverViewHolder extends BaseViewHolder {

        private final ActivityChatItemReceiverBinding binding;

        public ReceiverViewHolder(@NonNull ActivityChatItemReceiverBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        void bind(@NonNull ChatMessageItemViewState viewState) {
            binding.chatItemReceiverMessage.setText(viewState.getContent());
            binding.chatItemReceiverTimestamp.setText(viewState.getTimestamp().toString());
        }
    }

    public static class SenderViewHolder extends BaseViewHolder {

        private final ActivityChatItemSenderBinding binding;

        public SenderViewHolder(@NonNull ActivityChatItemSenderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        void bind(@NonNull ChatMessageItemViewState viewState) {
            binding.chatItemSenderMessage.setText(viewState.getContent());
            binding.chatItemSenderTimestamp.setText(viewState.getTimestamp().toString());
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
