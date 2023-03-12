package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.chat.ChatMessageResponse;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        chatRepository.createConversation(workmateId);
    }

    public LiveData<ChatViewState> getChatViewStateLiveData() {
        return Transformations.switchMap(
            chatRepository.getMessages(workmateId),
            entity -> {
                if (entity != null && workmateId != null) {
                    return new MutableLiveData<>(
                        new ChatViewState(workmateId, "", mapMessages(entity.getMessages()))
                    );
                }
                return null;
            }
        );
    }


    @NonNull
    private List<ChatMessageItemViewState> mapMessages(@NonNull List<ChatMessageResponse> messages) {
        List<ChatMessageItemViewState> messageItemViewStates = new ArrayList<>();
        for (ChatMessageResponse message : messages) {
            ChatMessageItemViewState itemViewState =
                new ChatMessageItemViewState(convertTimestamp(message.getTimestamp()), message.getSender(), message.getContent());
            messageItemViewStates.add(itemViewState);
        }
        return messageItemViewStates;
    }

    @NonNull
    private String convertTimestamp(@NonNull Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timestamp.toDate());
    }

    public void sendMessage(@NonNull String content) {
        if (content.length() > 0) {
            chatRepository.sendMessage(content, workmateId);
        }
    }
}
