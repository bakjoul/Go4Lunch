package com.bakjoul.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.data.chat.ChatItemViewType;
import com.bakjoul.go4lunch.databinding.ActivityChatItemDateBinding;
import com.bakjoul.go4lunch.databinding.ActivityChatItemReceivedBinding;
import com.bakjoul.go4lunch.databinding.ActivityChatItemSentBinding;

public class ChatAdapter extends ListAdapter<ChatItemViewState, ChatAdapter.BaseViewHolder> {

    public ChatAdapter() {
        super(new ChatAdapterDiffCallback());
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (ChatItemViewType.values()[viewType]) {
            case DATE_HEADER:
                return new DateHeaderViewHolder(ActivityChatItemDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case RECEIVED_MESSAGE:
                return new ReceiverViewHolder(ActivityChatItemReceivedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case SENT_MESSAGE:
                return new SenderViewHolder(ActivityChatItemSentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemViewType();
    }

    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(ChatItemViewState viewState);
    }

    public static class DateHeaderViewHolder extends BaseViewHolder {

        private final ActivityChatItemDateBinding binding;

        public DateHeaderViewHolder(@NonNull ActivityChatItemDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        void bind(ChatItemViewState viewState) {
            if (viewState instanceof ChatItemViewState.DateHeader) {
                ChatItemViewState.DateHeader dateHeaderItem = (ChatItemViewState.DateHeader) viewState;
                binding.chatItemDate.setText(dateHeaderItem.getDate());
            }
        }
    }

    public static class ReceiverViewHolder extends BaseViewHolder {

        private final ActivityChatItemReceivedBinding binding;

        public ReceiverViewHolder(@NonNull ActivityChatItemReceivedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        void bind(ChatItemViewState viewState) {
            if (viewState instanceof ChatItemViewState.ChatMessage) {
                ChatItemViewState.ChatMessage messageItem = (ChatItemViewState.ChatMessage) viewState;
                binding.chatItemReceiverMessage.setText(messageItem.getContent());
                binding.chatItemReceiverTimestamp.setText(messageItem.getTimestamp());
            }
        }
    }

    public static class SenderViewHolder extends BaseViewHolder {

        private final ActivityChatItemSentBinding binding;

        public SenderViewHolder(@NonNull ActivityChatItemSentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        void bind(ChatItemViewState viewState) {
            if (viewState instanceof ChatItemViewState.ChatMessage) {
                ChatItemViewState.ChatMessage messageItem = (ChatItemViewState.ChatMessage) viewState;
                binding.chatItemSenderMessage.setText(messageItem.getContent());
                binding.chatItemSenderTimestamp.setText(messageItem.getTimestamp());
            }
        }
    }

    private static class ChatAdapterDiffCallback extends DiffUtil.ItemCallback<ChatItemViewState> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatItemViewState oldItem, @NonNull ChatItemViewState newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatItemViewState oldItem, @NonNull ChatItemViewState newItem) {
            return oldItem.equals(newItem);
        }
    }

}
