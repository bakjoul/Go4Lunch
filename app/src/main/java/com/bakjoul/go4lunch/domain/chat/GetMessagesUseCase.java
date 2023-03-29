package com.bakjoul.go4lunch.domain.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;
import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;

import java.util.List;

import javax.inject.Inject;

public class GetMessagesUseCase {

    private static final String TAG = "GetMessagesUseCase";

    @NonNull
    private final ChatRepository chatRepository;

    @NonNull
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @Inject
    public GetMessagesUseCase(
        @NonNull ChatRepository chatRepository,
        @NonNull GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.chatRepository = chatRepository;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @NonNull
    public LiveData<List<ChatMessageEntity>> invoke(String receiverId) {
        final LoggedUserEntity currentUser = getCurrentUserUseCase.invoke();

        if (currentUser == null) {
            Log.d(TAG, "User is not logged in");
            return new MutableLiveData<>(null);
        } else {
            return chatRepository.getMessages(currentUser.getId(), receiverId);
        }
    }
}
