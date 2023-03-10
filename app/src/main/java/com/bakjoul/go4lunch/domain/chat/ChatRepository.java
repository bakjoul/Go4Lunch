package com.bakjoul.go4lunch.domain.chat;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface ChatRepository {

    LiveData<List<ChatThreadEntity>> getMessages(String workmateId);

    void sendMessage(String content, String workmateId);
}
