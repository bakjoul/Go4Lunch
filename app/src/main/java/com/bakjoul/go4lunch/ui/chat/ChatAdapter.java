package com.bakjoul.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.data.chat.ChatMessageItemType;
import com.bakjoul.go4lunch.databinding.ActivityChatItemDateBinding;
import com.bakjoul.go4lunch.databinding.ActivityChatItemReceiverBinding;
import com.bakjoul.go4lunch.databinding.ActivityChatItemSenderBinding;

public class ChatAdapter extends ListAdapter<ChatMessageItemViewState, RecyclerView.ViewHolder> {

    private static final int DATE_HEADER_VIEW_TYPE = 0;
    private static final int RECEIVED_MESSAGE_VIEW_TYPE = 1;
    private static final int SENT_MESSAGE_VIEW_TYPE = 2;

    public ChatAdapter() {
        super(new ChatAdapterDiffCallback());
    }

    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(ChatMessageItemViewState viewState);
    }

    public static class DateHeaderViewHolder extends BaseViewHolder {

        private final ActivityChatItemDateBinding binding;

        public DateHeaderViewHolder(@NonNull ActivityChatItemDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        void bind(@NonNull ChatMessageItemViewState viewState) {
            binding.chatItemDate.setText("");
        }
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
            binding.chatItemReceiverTimestamp.setText(viewState.getTimestamp());
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
            binding.chatItemSenderTimestamp.setText(viewState.getTimestamp());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case DATE_HEADER_VIEW_TYPE:
                return new DateHeaderViewHolder(ActivityChatItemDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case RECEIVED_MESSAGE_VIEW_TYPE:
                return new ReceiverViewHolder(ActivityChatItemReceiverBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case SENT_MESSAGE_VIEW_TYPE:
                return new SenderViewHolder(ActivityChatItemSenderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case DATE_HEADER_VIEW_TYPE:
                ((DateHeaderViewHolder) holder).bind(getItem(position));
                break;
            case RECEIVED_MESSAGE_VIEW_TYPE:
                ((ReceiverViewHolder) holder).bind(getItem(position));
                break;
            case SENT_MESSAGE_VIEW_TYPE:
                ((SenderViewHolder) holder).bind(getItem(position));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + holder.getItemViewType());
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageItemViewState itemViewState = getItem(position);
        if (itemViewState.getItemType() == ChatMessageItemType.RECEIVED) {
            return RECEIVED_MESSAGE_VIEW_TYPE;
        } else if (itemViewState.getItemType() == ChatMessageItemType.SENT) {
            return SENT_MESSAGE_VIEW_TYPE;
        }
        throw new IllegalArgumentException("Unknown message item view type");
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
