package com.bakjoul.go4lunch.data.chat;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class ChatMessageResponse {

    private final String content;

    private final String sender;

    private final com.google.firebase.Timestamp timestamp;

    public ChatMessageResponse() {
        this(null, null, null);
    }

    public ChatMessageResponse(String content, String sender, Timestamp timestamp) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageResponse message = (ChatMessageResponse) o;
        return Objects.equals(content, message.content) && Objects.equals(sender, message.sender) && Objects.equals(timestamp, message.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, sender, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessage{" +
            "content='" + content + '\'' +
            ", sender='" + sender + '\'' +
            ", timeStamp=" + timestamp +
            '}';
    }
}
