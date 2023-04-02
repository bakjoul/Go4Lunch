package com.bakjoul.go4lunch.domain.chat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.domain.auth.GetCurrentUserUseCase;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class SendMessageUseCaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final ChatRepository chatRepository = Mockito.mock(ChatRepository.class);

    private final GetCurrentUserUseCase getCurrentUserUseCase = Mockito.mock(GetCurrentUserUseCase.class);

    private final LoggedUserEntity mockedUser = Mockito.mock(LoggedUserEntity.class);

    private SendMessageUseCase sendMessageUseCase;

    @Before
    public void setUp() {
        doReturn(mockedUser).when(getCurrentUserUseCase).invoke();
        doReturn("mockedUserId").when(mockedUser).getId();

        sendMessageUseCase = new SendMessageUseCase(chatRepository, getCurrentUserUseCase);
    }

    @Test
    public void current_user_null_should_do_nothing() {
        // Given
        doReturn(null).when(getCurrentUserUseCase).invoke();

        // When
        sendMessageUseCase.invoke("fakeId", "fakeMessage");

        // Then
        verifyNoInteractions(chatRepository);
    }

    @Test
    public void current_user_not_null_should_send_message() {
        // Given
        doReturn(mockedUser).when(getCurrentUserUseCase).invoke();

        // When
        sendMessageUseCase.invoke("fakeId", "fakeMessage");

        // Then
        verify(chatRepository).sendMessage("mockedUserId", "fakeId", "fakeMessage");
        verifyNoMoreInteractions(chatRepository);
    }
}
