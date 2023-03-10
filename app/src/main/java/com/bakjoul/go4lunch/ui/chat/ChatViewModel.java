package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.chat.ChatMessage;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatViewModel extends ViewModel {

    private static final String KEY = "workmateId";
    private final String workmateId;

    @NonNull
    private final ChatRepository chatRepository;

    @Inject
    public ChatViewModel(
        @NonNull ChatRepository chatRepository,
        @NonNull SavedStateHandle savedStateHandle
    ) {
        this.chatRepository = chatRepository;

        workmateId = savedStateHandle.get(KEY);
    }

    public LiveData<ChatViewState> getChatViewStateLiveData() {
        return Transformations.switchMap(
            chatRepository.getMessages(workmateId),
            entity -> {
                if (entity != null && entity.get(0) != null && workmateId != null) {
                    return new MutableLiveData<>(
                        new ChatViewState(workmateId, "", mapMessages(entity.get(0).getMessages()))
                    );
                }
                return null;
            }
        );
    }


    @NonNull
    private List<ChatMessageItemViewState> mapMessages(@NonNull List<ChatMessage> messages) {
        List<ChatMessageItemViewState> messageItemViewStates = new ArrayList<>();
        for (ChatMessage message : messages) {
            ChatMessageItemViewState itemViewState =
                new ChatMessageItemViewState(message.getTimeStamp(), message.getSender(), message.getContent());
        }
        return messageItemViewStates;
    }

    public void sendMessage(@NonNull String content) {
        if (content.length() > 0) {
            chatRepository.sendMessage(content, workmateId);
        }
    }
}
