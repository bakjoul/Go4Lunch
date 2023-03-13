package com.bakjoul.go4lunch.data.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreChatQueryLiveData;
import com.bakjoul.go4lunch.domain.chat.ChatMessageEntity;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChatRepositoryImplementation implements ChatRepository {

    private static final String TAG = "ChatRepositoryImplement";

    @NonNull
    private final FirebaseFirestore firestoreDb;

    @Inject
    public ChatRepositoryImplementation(@NonNull FirebaseFirestore firestoreDb) {
        this.firestoreDb = firestoreDb;
    }

    @Override
    public LiveData<List<ChatMessageEntity>> getMessages(String sender, String receiver) {
        if (sender == null && receiver == null) {
            return new MutableLiveData<>();
        } else {
            return new FirestoreChatQueryLiveData<ChatMessageResponse, ChatMessageEntity>(
                firestoreDb.collection("chats").document(getChatId(sender, receiver)).collection("chat")
                    .orderBy("timestamp", Query.Direction.DESCENDING),
                ChatMessageResponse.class
            ) {
                @Override
                public ChatMessageEntity map(ChatMessageResponse response, String responseId) {
                    final ChatMessageEntity entity;

                    if (response.getSender() != null
                        && response.getContent() != null
                        && response.getTimestamp() != null) {
                        entity = new ChatMessageEntity(
                            responseId,
                            response.getSender(),
                            response.getContent(),
                            response.getTimestamp()
                        );
                    } else {
                        entity = null;
                    }

                    return entity;
                }
            };
        }
    }

    @Override
    public void sendMessage(String sender, String receiver, String content) {
        if (sender != null && receiver != null) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("id", null);
            messageMap.put("sender", sender);
            messageMap.put("content", content);
            messageMap.put("timestamp", FieldValue.serverTimestamp());

            firestoreDb.collection("chats")
                .document(getChatId(sender, receiver))
                .collection("chat")
                .add(messageMap)
                .addOnCompleteListener(task -> {
                    if (task.getException() == null) {
                        Log.d(TAG, "Message sent to " + receiver);
                    } else {
                        Log.d(TAG, "Message could not be sent: " + task.getException().getMessage());
                    }
                });
        }
    }

    @NonNull
    private String getChatId(String sender, String receiver) {
        if (sender != null
            && sender.compareTo(receiver) < 0) {
            return sender + receiver;
        } else {
            return receiver + sender;
        }
    }
}
