package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class ChatViewState {

    @NonNull
    private final String photoUrl;

    @NonNull
    private final String workmateUsername;

    @NonNull
    private final List<ChatMessageItemViewState> messageItemViewStates;

    public ChatViewState(@NonNull String photoUrl, @NonNull String workmateUsername, @NonNull List<ChatMessageItemViewState> messageItemViewStates) {
        this.photoUrl = photoUrl;
        this.workmateUsername = workmateUsername;
        this.messageItemViewStates = messageItemViewStates;
    }

    @NonNull
    public String getPhotoUrl() {
        return photoUrl;
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
        return photoUrl.equals(that.photoUrl) && workmateUsername.equals(that.workmateUsername) && messageItemViewStates.equals(that.messageItemViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, workmateUsername, messageItemViewStates);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatViewState{" +
            "photoUrl='" + photoUrl + '\'' +
            ", workmateUsername='" + workmateUsername + '\'' +
            ", messageItemViewStates=" + messageItemViewStates +
            '}';
    }
}
