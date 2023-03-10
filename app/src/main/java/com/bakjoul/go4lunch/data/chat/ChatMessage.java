package com.bakjoul.go4lunch.data.chat;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class ChatMessage {

    @NonNull
    private final String content;

    @NonNull
    private final String sender;

    @NonNull
    private final com.google.firebase.Timestamp timeStamp;

    public ChatMessage(@NonNull String content, @NonNull String sender, @NonNull com.google.firebase.Timestamp timeStamp) {
        this.content = content;
        this.sender = sender;
        this.timeStamp = timeStamp;
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
    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return content.equals(that.content) && sender.equals(that.sender) && timeStamp.equals(that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, sender, timeStamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessage{" +
            "content='" + content + '\'' +
            ", sender='" + sender + '\'' +
            ", timeStamp=" + timeStamp +
            '}';
    }
}
