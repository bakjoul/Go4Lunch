package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class ChatViewState {

    @NonNull
    private final String workmateId;

    @NonNull
    private final String workmateUsername;

    @NonNull
    private final List<ChatMessageItemViewState> messageItemViewStates;

    public ChatViewState(@NonNull String workmateId, @NonNull String workmateUsername, @NonNull List<ChatMessageItemViewState> messageItemViewStates) {
        this.workmateId = workmateId;
        this.workmateUsername = workmateUsername;
        this.messageItemViewStates = messageItemViewStates;
    }

    @NonNull
    public String getWorkmateId() {
        return workmateId;
    }

    @NonNull
    public String getWorkmateUsername() {
        return workmateUsername;
    }

    @NonNull
    public List<ChatMessageItemViewState> getMessageItemViewStates() {
        return messageItemViewStates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatViewState that = (ChatViewState) o;
        return workmateId.equals(that.workmateId) && workmateUsername.equals(that.workmateUsername) && messageItemViewStates.equals(that.messageItemViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workmateId, workmateUsername, messageItemViewStates);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatViewState{" +
            "workmateId='" + workmateId + '\'' +
            ", workmateUsername='" + workmateUsername + '\'' +
            ", messageItemViewStates=" + messageItemViewStates +
            '}';
    }
}
