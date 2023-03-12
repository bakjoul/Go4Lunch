package com.bakjoul.go4lunch.data.chat;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class ChatThreadResponse {

    @Nullable
    private final String id;

    @Nullable
    private final List<ChatMessageResponse> messages;

    public ChatThreadResponse() {
        this(null, null);
    }

    public ChatThreadResponse(@Nullable String id, @Nullable List<ChatMessageResponse> messages) {
        this.id = id;
        this.messages = messages;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public List<ChatMessageResponse> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatThreadResponse that = (ChatThreadResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, messages);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatThreadResponse{" +
            "id='" + id + '\'' +
            ", messages=" + messages +
            '}';
    }
}
