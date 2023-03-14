package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.chat.ChatItemViewType;

import java.util.Objects;

public abstract class ChatItemViewState {
    @NonNull
    private final String id;

    protected ChatItemViewState(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatItemViewState that = (ChatItemViewState) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatItemViewState{" +
            "id='" + id + '\'' +
            '}';
    }

    public abstract int getItemViewType();

    public static class DateHeader extends ChatItemViewState {

        @NonNull
        private final String date;

        public DateHeader(@NonNull String id, @NonNull String date) {
            super(id);
            this.date = date;
        }

        @NonNull
        public String getDate() {
            return date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            DateHeader that = (DateHeader) o;
            return date.equals(that.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), date);
        }

        @NonNull
        @Override
        public String toString() {
            return "DateHeader{" +
                "id='" + getId() + '\'' +
                ", date='" + date + '\'' +
                '}';
        }

        @Override
        public int getItemViewType() {
            return ChatItemViewType.DATE_HEADER.ordinal();
        }
    }

    public static class ChatMessage extends ChatItemViewState {
        @NonNull
        private final ChatItemViewType itemType;

        @NonNull
        private final String sender;

        @NonNull
        private final String content;

        @NonNull
        private final String timestamp;

        public ChatMessage(@NonNull String id, @NonNull ChatItemViewType itemType, @NonNull String sender, @NonNull String content, @NonNull String timestamp) {
            super(id);
            this.itemType = itemType;
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }

        @NonNull
        public ChatItemViewType getItemType() {
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
            if (!super.equals(o)) return false;
            ChatMessage that = (ChatMessage) o;
            return itemType == that.itemType && sender.equals(that.sender) && content.equals(that.content) && timestamp.equals(that.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), itemType, sender, content, timestamp);
        }

        @NonNull
        @Override
        public String toString() {
            return "ChatMessage{" +
                "id='" + getId() + '\'' +
                ", itemType=" + itemType +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
        }

        @Override
        public int getItemViewType() {
            return getItemType().ordinal();
        }
    }
}
