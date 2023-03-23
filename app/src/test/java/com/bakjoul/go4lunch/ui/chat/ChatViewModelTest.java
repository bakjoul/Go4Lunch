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

import com.bakjoul.go4lunch.domain.chat.ChatMessageEntity;
import com.bakjoul.go4lunch.domain.chat.GetMessagesUseCase;
import com.bakjoul.go4lunch.domain.chat.SendMessageUseCase;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final GetMessagesUseCase getMessagesUseCase = Mockito.mock(GetMessagesUseCase.class);

    private final SendMessageUseCase sendMessageUseCase = Mockito.mock(SendMessageUseCase.class);

    private final SavedStateHandle savedStateHandle = Mockito.mock(SavedStateHandle.class);

    private final MutableLiveData<List<ChatMessageEntity>> messagesLiveData = new MutableLiveData<>();

    private ChatViewModel viewModel;

    @Before
    public void setUp() {
        doReturn("workmateId").when(savedStateHandle).get("workmateId");
        doReturn("fakePhotoUrl").when(savedStateHandle).get("photoUrl");
        doReturn("fakeUsername").when(savedStateHandle).get("username");
        doReturn(messagesLiveData).when(getMessagesUseCase).invoke(anyString());

        viewModel = new ChatViewModel(getMessagesUseCase, sendMessageUseCase, savedStateHandle);
    }

    @Test
    public void initial_case() {
        // Given
        messagesLiveData.setValue(new ArrayList<>());

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
            new ArrayList<>()
        );
    }

    @NonNull
    private ChatViewState getEmptyChatViewState() {
        return new ChatViewState("", "", new ArrayList<>());
    }
    // endregion
}