package com.bakjoul.go4lunch.domain.chat;

import androidx.lifecycle.LiveData;

public interface ChatRepository {

    void createConversation(String workmateId);

    LiveData<ChatThreadEntity> getMessages(String workmateId);

    void sendMessage(String content, String workmateId);
}
