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
    private final List<ChatItemViewState> chatItemViewStates;

    public ChatViewState(@NonNull String photoUrl, @NonNull String workmateUsername, @NonNull List<ChatItemViewState> chatItemViewStates) {
        this.photoUrl = photoUrl;
        this.workmateUsername = workmateUsername;
        this.chatItemViewStates = chatItemViewStates;
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
    public List<ChatItemViewState> getChatItemViewStates() {
        return chatItemViewStates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatViewState that = (ChatViewState) o;
        return photoUrl.equals(that.photoUrl) && workmateUsername.equals(that.workmateUsername) && chatItemViewStates.equals(that.chatItemViewStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, workmateUsername, chatItemViewStates);
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatViewState{" +
            "photoUrl='" + photoUrl + '\'' +
            ", workmateUsername='" + workmateUsername + '\'' +
            ", chatItemViewStates=" + chatItemViewStates +
            '}';
    }
}
