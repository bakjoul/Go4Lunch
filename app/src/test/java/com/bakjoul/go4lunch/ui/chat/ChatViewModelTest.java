package com.bakjoul.go4lunch.ui.chat;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.bakjoul.go4lunch.data.chat.ChatItemViewType;
import com.bakjoul.go4lunch.domain.chat.ChatMessageEntity;
import com.bakjoul.go4lunch.domain.chat.GetMessagesUseCase;
import com.bakjoul.go4lunch.domain.chat.SendMessageUseCase;
import com.bakjoul.go4lunch.ui.utils.RandomUUIDUtil;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ChatViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GetMessagesUseCase getMessagesUseCase = Mockito.mock(GetMessagesUseCase.class);

    private final SendMessageUseCase sendMessageUseCase = Mockito.mock(SendMessageUseCase.class);

    private final SavedStateHandle savedStateHandle = Mockito.mock(SavedStateHandle.class);

    private final RandomUUIDUtil randomUUIDUtil = Mockito.mock(RandomUUIDUtil.class);

    private final MutableLiveData<List<ChatMessageEntity>> messagesLiveData = new MutableLiveData<>();

    private final LocalDateTime todayLocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 27));

    private final Date today = Date.from(todayLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

    private final Date pastDate = Date.from(todayLocalDateTime.minusDays(8).atZone(ZoneId.systemDefault()).toInstant());

    private ChatViewModel viewModel;

    @Before
    public void setUp() {
        doReturn("workmateId").when(savedStateHandle).get("workmateId");
        doReturn("fakePhotoUrl").when(savedStateHandle).get("photoUrl");
        doReturn("fakeUsername").when(savedStateHandle).get("username");
        doReturn(messagesLiveData).when(getMessagesUseCase).invoke(anyString());
        doReturn("fakeUUID").when(randomUUIDUtil).generateUUID();

        viewModel = new ChatViewModel(getMessagesUseCase, sendMessageUseCase, savedStateHandle, randomUUIDUtil);
    }

    @Test
    public void initial_case() {
        // Given
        messagesLiveData.setValue(getDefaultMessagesList());

        // When
        ChatViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getChatViewStateLiveData());

        // Then
        assertEquals(getDefaultChatViewState(), result);
    }

    @Test
    public void messagesList_null_case() {
        // Given
        messagesLiveData.setValue(null);

        // When
        ChatViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getChatViewStateLiveData());

        // Then
        assertEquals(getEmptyChatViewState(), result);
    }

    @Test
    public void sendMessage() {
        // When
        viewModel.sendMessage("fakeMessage");

        // Then
        verify(sendMessageUseCase).invoke("workmateId", "fakeMessage");
        verifyNoMoreInteractions(sendMessageUseCase);
    }

    @Test
    public void sendInvalidMessage() {
        // When
        viewModel.sendMessage("");

        // Then
        verifyNoMoreInteractions(sendMessageUseCase);
    }

    // region OUT
    @NonNull
    private ChatViewState getDefaultChatViewState() {
        return new ChatViewState(
            "fakePhotoUrl",
            "fakeUsername",
            getDefaultChatItemViewStateList()
        );
    }

    @NonNull
    private ChatViewState getEmptyChatViewState() {
        return new ChatViewState("", "", new ArrayList<>());
    }

    @NonNull
    private List<ChatMessageEntity> getDefaultMessagesList() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(pastDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        return Arrays.asList(
            new ChatMessageEntity(
                "fakeMessageId2",
                "fakeUser2",
                "fakeMessage2",
                new Timestamp(today)
            ),
            new ChatMessageEntity(
                "fakeMessageId1",
                "workmateId",
                "fakeMessage1",
                new Timestamp(pastDate)
            )
        );
    }

    @NonNull
    private List<ChatItemViewState> getDefaultChatItemViewStateList() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("d MMMM", Locale.getDefault());
        return Arrays.asList(
            new ChatItemViewState.ChatMessage(
                "fakeMessageId2",
                ChatItemViewType.SENT_MESSAGE,
                "fakeUser2",
                "fakeMessage2",
                "18:27",
                "fakePhotoUrl"
            ),
            new ChatItemViewState.DateHeader(
                "fakeUUID",
                dateFormat.format(today).toUpperCase()
            ),
            new ChatItemViewState.ChatMessage(
                "fakeMessageId1",
                ChatItemViewType.RECEIVED_MESSAGE,
                "workmateId",
                "fakeMessage1",
                "18:27",
                "fakePhotoUrl"
            ),
            new ChatItemViewState.DateHeader(
                "fakeUUID",
                dateFormat2.format(pastDate).toUpperCase()
            )
        );
    }
    // endregion
}