package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.chat.ChatMessageViewType;

import java.util.Objects;

public class ChatMessageItemViewState {

    @NonNull
    private final ChatMessageViewType itemType;

    @NonNull
    private final String sender;

    @NonNull
    private final String content;

    @NonNull
    private final String timestamp;

    public ChatMessageItemViewState(@NonNull ChatMessageViewType itemType, @NonNull String sender, @NonNull String content, @NonNull String timestamp) {
        this.itemType = itemType;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    @NonNull
    public ChatMessageViewType getItemType() {
        return itemType;
    }

    @NonNull
    public String getSender() {
        return sender;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @NonNull
    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageItemViewState that = (ChatMessageItemViewState) o;
        return itemType == that.itemType && sender.equals(that.sender) && content.equals(that.content) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType, sender, content, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessageItemViewState{" +
            "itemType=" + itemType +
            ", sender='" + sender + '\'' +
            ", content='" + content + '\'' +
            ", timestamp='" + timestamp + '\'' +
            '}';
    }
}
