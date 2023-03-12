package com.bakjoul.go4lunch.domain.chat;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.chat.ChatMessageResponse;

import java.util.List;
import java.util.Objects;

public class ChatThreadEntity {

    @NonNull
    private final String id;

    @NonNull
    private final List<ChatMessageResponse> messages;

    public ChatThreadEntity(@NonNull String id, @NonNull List<ChatMessageResponse> messages) {
        this.id = id;
        this.messages = messages;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public List<ChatMessageResponse> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatThreadEntity that = (ChatThreadEntity) o;
        return id.equals(that.id) && messages.equals(that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, messages);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatThreadEntity{" +
            "id='" + id + '\'' +
            ", messages=" + messages +
            '}';
    }
}
