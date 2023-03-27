package com.bakjoul.go4lunch.domain.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;

import java.util.List;

import javax.inject.Inject;

public class GetMessagesUseCase {

    private static final String TAG = "GetMessagesUseCase";

    @NonNull
    private final ChatRepository chatRepository;

    @NonNull
    private final AuthRepository authRepository;

    @Inject
    public GetMessagesUseCase(
        @NonNull ChatRepository chatRepository,
        @NonNull AuthRepository authRepository
    ) {
        this.chatRepository = chatRepository;
        this.authRepository = authRepository;
    }

    public LiveData<List<ChatMessageEntity>> invoke(String receiverId) {
        String currentUserId = null;
        if (authRepository.getCurrentUser() != null) {
            currentUserId = authRepository.getCurrentUser().getId();
        }

        if (currentUserId == null) {
            Log.d(TAG, "User is not logged in");
            return null;
        } else {
            return chatRepository.getMessages(currentUserId, receiverId);
        }
    }
}
