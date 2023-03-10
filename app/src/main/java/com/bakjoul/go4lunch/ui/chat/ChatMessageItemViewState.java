package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class ChatMessageItemViewState {

    @NonNull
    private final Timestamp timestamp;

    @NonNull
    private final String sender;

    @NonNull
    private final String content;

    public ChatMessageItemViewState(@NonNull Timestamp timestamp, @NonNull String sender, @NonNull String content) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.content = content;
    }

    @NonNull
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @NonNull
    public String getSender() {
        return sender;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageItemViewState that = (ChatMessageItemViewState) o;
        return timestamp.equals(that.timestamp) && sender.equals(that.sender) && content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, sender, content);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessageItemViewState{" +
            "timestamp=" + timestamp +
            ", sender='" + sender + '\'' +
            ", content='" + content + '\'' +
            '}';
    }
}
