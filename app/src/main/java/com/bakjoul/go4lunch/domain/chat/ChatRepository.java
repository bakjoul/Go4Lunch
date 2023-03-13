package com.bakjoul.go4lunch.domain.chat;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface ChatRepository {

    LiveData<List<ChatMessageEntity>> getMessages(String sender, String receiver);

    void sendMessage(String sender, String receiver, String content);
}
