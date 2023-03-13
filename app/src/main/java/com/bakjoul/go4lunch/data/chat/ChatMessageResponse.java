package com.bakjoul.go4lunch.data.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Objects;

public class ChatMessageResponse {

    @Nullable
    private final String id;

    @Nullable
    private final String sender;

    @Nullable
    private final String content;

    @Nullable
    @ServerTimestamp
    private final Timestamp timestamp;

    public ChatMessageResponse() {
        this(null, null, null, null);
    }

    public ChatMessageResponse(@Nullable String id, @Nullable String sender, @Nullable String content, @Nullable Timestamp timestamp) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public String getSender() {
        return sender;
    }

    @Nullable
    public String getContent() {
        return content;
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
        return Objects.equals(id, that.id) && Objects.equals(sender, that.sender) && Objects.equals(content, that.content) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, content, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessageResponse{" +
            "id='" + id + '\'' +
            ", sender='" + sender + '\'' +
            ", content='" + content + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}
