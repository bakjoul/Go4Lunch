package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.chat.ChatMessageViewType;
import com.bakjoul.go4lunch.domain.chat.ChatMessageEntity;
import com.bakjoul.go4lunch.domain.chat.GetMessagesUseCase;
import com.bakjoul.go4lunch.domain.chat.SendMessageUseCase;
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
    private final GetMessagesUseCase getMessagesUseCase;

    @NonNull
    private final SendMessageUseCase sendMessageUseCase;

    @Inject
    public ChatViewModel(
        @NonNull GetMessagesUseCase getMessagesUseCase,
        @NonNull SendMessageUseCase sendMessageUseCase,
        @NonNull SavedStateHandle savedStateHandle
    ) {
        this.getMessagesUseCase = getMessagesUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
        workmateId = savedStateHandle.get(KEY);
    }

    public LiveData<ChatViewState> getChatViewStateLiveData() {
        return Transformations.switchMap(
            getMessagesUseCase.invoke(workmateId),
            entity -> {
                if (entity != null && workmateId != null) {
                    return new MutableLiveData<>(
                        new ChatViewState(workmateId, "", mapMessages(entity))
                    );
                }
                return new MutableLiveData<>(new ChatViewState("", "", new ArrayList<>()));
            }
        );
    }

    public void sendMessage(@NonNull String content) {
        if (content.length() > 0) {
            sendMessageUseCase.invoke(workmateId, content);
        }
    }

    @NonNull
    private List<ChatMessageItemViewState> mapMessages(@NonNull List<ChatMessageEntity> messageEntityList) {
        List<ChatMessageItemViewState> messageItemViewStates = new ArrayList<>();

        for (ChatMessageEntity message : messageEntityList) {
            ChatMessageItemViewState itemViewState =
                new ChatMessageItemViewState(
                    message.getId(),
                    getItemType(message),
                    message.getSender(),
                    message.getContent(),
                    convertTimestamp(message.getTimestamp())
                );
            messageItemViewStates.add(itemViewState);
        }

        return messageItemViewStates;
    }

    private ChatMessageViewType getItemType(@NonNull ChatMessageEntity message) {
        if (!message.getSender().equals(workmateId)) {
            return ChatMessageViewType.SENT_MESSAGE_VIEW_TYPE;
        } else {
            return ChatMessageViewType.RECEIVED_MESSAGE_VIEW_TYPE;
        }
    }

    @NonNull
    private String convertTimestamp(@NonNull Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timestamp.toDate());
    }
}
