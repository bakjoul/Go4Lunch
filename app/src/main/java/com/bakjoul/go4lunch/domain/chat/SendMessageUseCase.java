package com.bakjoul.go4lunch.domain.chat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;
import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;

import javax.inject.Inject;

public class SendMessageUseCase {

    private static final String TAG = "SendMessageUseCase";

    @NonNull
    private final ChatRepository chatRepository;

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @Inject
    public SendMessageUseCase(
        @NonNull ChatRepository chatRepository,
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.chatRepository = chatRepository;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    public void invoke(String receiverId, String content) {
        final LoggedUserEntity currentUser = getCurrentUserUseCase.invoke();

        if (currentUser == null) {
            Log.d(TAG, "User is not logged in");
        } else {
            chatRepository.sendMessage(currentUser.getId(), receiverId, content);
        }
    }
}
