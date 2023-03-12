package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.chat.ChatMessageItemType;
import com.bakjoul.go4lunch.data.chat.ChatMessageResponse;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;
import com.bakjoul.go4lunch.domain.chat.SendMessageUseCase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

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
    private final SendMessageUseCase sendMessageUseCase;

    @NonNull
    private final ChatRepository chatRepository; // TODO Bakjoul remove any repo dependency from VMs, use UseCase only!

    @Inject
    public ChatViewModel(
        @NonNull SendMessageUseCase sendMessageUseCase,
        @NonNull ChatRepository chatRepository,
        @NonNull SavedStateHandle savedStateHandle
    ) {
        this.sendMessageUseCase = sendMessageUseCase;
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

    public void sendMessage(@NonNull String content) {
        if (content.length() > 0) {
            sendMessageUseCase.invoke(workmateId, content);
        }
    }

    @NonNull
    private List<ChatMessageItemViewState> mapMessages(@NonNull List<ChatMessageResponse> messages) { // TODO Bakjoul ChatMessageResponse -> ChatMessageEntity
        List<ChatMessageItemViewState> messageItemViewStates = new ArrayList<>();
        for (ChatMessageResponse message : messages) {
            ChatMessageItemViewState itemViewState =
                new ChatMessageItemViewState(
                    getItemType(message),
                    message.getSender(),
                    message.getContent(),
                    convertTimestamp(message.getTimestamp())
                );
            messageItemViewStates.add(itemViewState);
        }
        return messageItemViewStates;
    }

    private ChatMessageItemType getItemType(@NonNull ChatMessageResponse message) {
       // if (message.getSender().equals(firebaseAuth.getUid())) { // TODO BAKJOUL Exprimer sender // receiver dans l'entity
            return ChatMessageItemType.SENT;
//        } else {
//            return ChatMessageItemType.RECEIVED;
//        }
    }

    @NonNull
    private String convertTimestamp(@NonNull Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timestamp.toDate());
    }
}