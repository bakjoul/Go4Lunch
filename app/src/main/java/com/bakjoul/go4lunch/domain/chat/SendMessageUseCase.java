package com.bakjoul.go4lunch.domain.chat;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;

import javax.inject.Inject;

public class SendMessageUseCase {

    @NonNull
    private final ChatRepository chatRepository;

    @NonNull
    private final AuthRepository authRepository;

    @Inject
    public SendMessageUseCase(
        @NonNull ChatRepository chatRepository,
        @NonNull AuthRepository authRepository
    ) {

        this.chatRepository = chatRepository;
        this.authRepository = authRepository;
    }

    public void invoke(String receiverId, String content) {
        String currentUserId = authRepository.getCurrentUserId();

        if (currentUserId == null) {
            // TODO Bakjoul handle not connected error
        } else {
            chatRepository.sendMessage(currentUserId, receiverId, content);
        }
    }
}
