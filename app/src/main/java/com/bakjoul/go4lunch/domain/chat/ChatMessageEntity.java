package com.bakjoul.go4lunch.domain.chat;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class ChatMessageEntity {

    @NonNull
    private final String content;

    @NonNull
    private final String sender;

    @NonNull
    private final Timestamp timestamp;

    public ChatMessageEntity(@NonNull String content, @NonNull String sender, @NonNull Timestamp timestamp) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @NonNull
    public String getSender() {
        return sender;
    }

    @NonNull
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageEntity that = (ChatMessageEntity) o;
        return content.equals(that.content) && sender.equals(that.sender) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, sender, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessageEntity{" +
            "content='" + content + '\'' +
            ", sender='" + sender + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}
