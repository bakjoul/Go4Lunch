package com.bakjoul.go4lunch.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.data.chat.ChatItemViewType;
import com.bakjoul.go4lunch.domain.chat.ChatMessageEntity;
import com.bakjoul.go4lunch.domain.chat.GetMessagesUseCase;
import com.bakjoul.go4lunch.domain.chat.SendMessageUseCase;
import com.bakjoul.go4lunch.ui.utils.RandomUUIDUtil;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatViewModel extends ViewModel {

    private static final String KEY_WORKMATE_ID = "workmateId";
    private static final String KEY_PHOTO_URL = "photoUrl";
    private static final String KEY_USERNAME = "username";

    private final String workmateId;
    private final String photoUrl;
    private final String username;

    @NonNull
    private final GetMessagesUseCase getMessagesUseCase;

    @NonNull
    private final SendMessageUseCase sendMessageUseCase;

    @NonNull
    private final RandomUUIDUtil randomUUIDUtil;

    @Inject
    public ChatViewModel(
        @NonNull GetMessagesUseCase getMessagesUseCase,
        @NonNull SendMessageUseCase sendMessageUseCase,
        @NonNull SavedStateHandle savedStateHandle,
        @NonNull RandomUUIDUtil randomUUIDUtil
    ) {
        this.getMessagesUseCase = getMessagesUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
        this.randomUUIDUtil = randomUUIDUtil;
        workmateId = savedStateHandle.get(KEY_WORKMATE_ID);
        photoUrl = savedStateHandle.get(KEY_PHOTO_URL);
        username = savedStateHandle.get(KEY_USERNAME);
    }

    public LiveData<ChatViewState> getChatViewStateLiveData() {
        return Transformations.switchMap(
            getMessagesUseCase.invoke(workmateId),
            entity -> {
                if (entity != null && workmateId != null) {
                    return new MutableLiveData<>(
                        new ChatViewState(photoUrl, username, mapMessages(entity))
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
    private List<ChatItemViewState> mapMessages(@NonNull List<ChatMessageEntity> messageEntityList) {
        List<ChatItemViewState> chatItemViewStates = new ArrayList<>();

        Date previousDate = null;

        // Loop through the messages list in reverse order to check if date changes first
        for (int i = messageEntityList.size() - 1; i >= 0; i--) {
            ChatMessageEntity message = messageEntityList.get(i);
            Date messageDate = message.getTimestamp().toDate();

            // If date has changed, adds a date header view state
            if (previousDate == null || !isSameDay(previousDate, messageDate)) {
                ChatItemViewState.DateHeader dateHeader = new ChatItemViewState.DateHeader(randomUUIDUtil.generateUUID(), formatDate(messageDate));
                chatItemViewStates.add(0, dateHeader);  // Adds the header to the beginning of the list
            }

            ChatItemViewState.ChatMessage chatMessage = new ChatItemViewState.ChatMessage(
                message.getId(),
                getItemType(message),
                message.getSender(),
                message.getContent(),
                convertTimestamp(message.getTimestamp()),
                photoUrl
            );
            chatItemViewStates.add(0, chatMessage); // Adds the message to the beginning of the list

            previousDate = messageDate;
        }

        return chatItemViewStates;
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return calendar.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && calendar.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @NonNull
    private String formatDate(@NonNull Date date) {
        Date currentDate = new Date();
        long diffInDays = TimeUnit.DAYS.convert(currentDate.getTime() - date.getTime(), TimeUnit.MILLISECONDS);

        SimpleDateFormat dateFormat;
        if (diffInDays >= 7) {
            dateFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("EEE d", Locale.getDefault());
        }
        return dateFormat.format(date).toUpperCase();
    }

    private ChatItemViewType getItemType(@NonNull ChatMessageEntity message) {
        if (!message.getSender().equals(workmateId)) {
            return ChatItemViewType.SENT_MESSAGE;
        } else {
            return ChatItemViewType.RECEIVED_MESSAGE;
        }
    }

    @NonNull
    private String convertTimestamp(@NonNull Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timestamp.toDate());
    }
}
