package com.bakjoul.go4lunch.data.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class ChatMessageResponse {

    @Nullable
    private final String content;

    @Nullable
    private final String sender;

    @Nullable
    private final com.google.firebase.Timestamp timestamp;

    public ChatMessageResponse() {
        this(null, null, null);
    }

    public ChatMessageResponse(@Nullable String content, @Nullable String sender, @Nullable Timestamp timestamp) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    @Nullable
    public String getSender() {
        return sender;
    }

    @Nullable
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageResponse that = (ChatMessageResponse) o;
        return Objects.equals(content, that.content) && Objects.equals(sender, that.sender) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, sender, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessageResponse{" +
            "content='" + content + '\'' +
            ", sender='" + sender + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}