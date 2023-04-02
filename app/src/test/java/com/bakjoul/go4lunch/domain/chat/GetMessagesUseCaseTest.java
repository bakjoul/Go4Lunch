package com.bakjoul.go4lunch.domain.chat;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class GetMessagesUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final ChatRepository chatRepository = Mockito.mock(ChatRepository.class);

    private final GetCurrentUserUseCase getCurrentUserUseCase = Mockito.mock(GetCurrentUserUseCase.class);

    private final LoggedUserEntity mockedUser = Mockito.mock(LoggedUserEntity.class);

    private final MutableLiveData<List<ChatMessageEntity>> messages = new MutableLiveData<>();

    private GetMessagesUseCase getMessagesUseCase;

    @Before
    public void setUp() {
        doReturn(mockedUser).when(getCurrentUserUseCase).invoke();
        doReturn("mockedUserId").when(mockedUser).getId();
        doReturn(messages).when(chatRepository).getMessages(anyString(), anyString());

        getMessagesUseCase = new GetMessagesUseCase(chatRepository, getCurrentUserUseCase);
    }

    @Test
    public void current_user_null_should_return_null() {
        // Given
        doReturn(null).when(getCurrentUserUseCase).invoke();

        // When
        List<ChatMessageEntity> result = LiveDataTestUtil.getValueForTesting(getMessagesUseCase.invoke("fakeId"));

        // Then
        assertNull(result);
    }

    @Test
    public void current_user_not_null_should_return_messages() {
        // Given
        messages.setValue(new ArrayList<>());

        // When
        List<ChatMessageEntity> result = LiveDataTestUtil.getValueForTesting(getMessagesUseCase.invoke("fakeId"));

        // Then
        assertEquals(new ArrayList<>(), result);
    }
}