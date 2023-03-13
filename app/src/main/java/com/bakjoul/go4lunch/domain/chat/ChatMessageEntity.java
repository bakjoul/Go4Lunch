package com.bakjoul.go4lunch.domain.chat;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Objects;
import java.util.UUID;

public class ChatMessageEntity {

    @NonNull
    private final String id;

    @NonNull
    private final String sender;

    @NonNull
    private final String content;

    @NonNull
    private final Timestamp timestamp;

    public ChatMessageEntity(@NonNull String id, @NonNull String sender, @NonNull String content, @NonNull Timestamp timestamp) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ChatMessageEntity(@NonNull String sender, @NonNull String content, @NonNull Timestamp timestamp) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getId() {
        return id;
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
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageEntity entity = (ChatMessageEntity) o;
        return id.equals(entity.id) && sender.equals(entity.sender) && content.equals(entity.content) && timestamp.equals(entity.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, content, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessageEntity{" +
            "id='" + id + '\'' +
            ", sender='" + sender + '\'' +
            ", content='" + content + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}
