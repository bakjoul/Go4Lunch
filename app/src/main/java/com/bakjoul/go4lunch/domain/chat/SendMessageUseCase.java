package com.bakjoul.go4lunch.domain.chat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;

import javax.inject.Inject;

public class SendMessageUseCase {

    private static final String TAG = "SendMessageUseCase";

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
        String currentUserId = null;
        if (authRepository.getCurrentUser() != null) {
            currentUserId = authRepository.getCurrentUser().getId();
        }

        if (currentUserId == null) {
            Log.d(TAG, "User is not logged in");
        } else {
            chatRepository.sendMessage(currentUserId, receiverId, content);
        }
    }
}
