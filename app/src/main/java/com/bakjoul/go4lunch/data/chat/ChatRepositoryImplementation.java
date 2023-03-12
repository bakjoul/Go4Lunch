package com.bakjoul.go4lunch.data.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreDocumentLiveData;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;
import com.bakjoul.go4lunch.domain.chat.ChatThreadEntity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChatRepositoryImplementation implements ChatRepository {

    private static final String TAG = "ChatRepositoryImplement";

    @NonNull
    private final FirebaseFirestore firestoreDb;

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    public ChatRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreDb = firestoreDb;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void createConversation(String workmateId) {
        String chatId = getChatId(workmateId);

        firestoreDb.collection("chats").document(chatId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Log.d(TAG, "Conversation already exists");
                    } else {
                        firestoreDb.collection("chats").document(chatId)
                            .set(new ChatThreadResponse(chatId, new ArrayList<>()))
                            .addOnCompleteListener(task1 -> Log.d(TAG, "Chat between " + firebaseAuth.getUid() + " and " + workmateId + "was created"))
                            .addOnFailureListener(e -> Log.e(TAG, "Chat could not be created. " + e.getMessage()));
                    }
                } else {
                    Log.e(TAG, "Error getting document", task.getException());
                }

            });
    }

    @NonNull
    private String getChatId(String workmateId) {
        if (firebaseAuth.getUid() != null
            && firebaseAuth.getUid().compareTo(workmateId) < 0) {
            return firebaseAuth.getUid() + workmateId;
        } else {
            return workmateId + firebaseAuth.getUid();
        }
    }

    @Override
    public LiveData<ChatThreadEntity> getMessages(String workmateId) {
        if (firebaseAuth.getUid() == null) {
            return new MutableLiveData<>();
        } else {
            return new FirestoreDocumentLiveData<ChatThreadResponse, ChatThreadEntity>(
                firestoreDb.collection("chats").document(getChatId(workmateId)),
                ChatThreadResponse.class
            ) {

                @Override
                public ChatThreadEntity map(ChatThreadResponse response) {
                    if (response == null) {
                        return null;
                    }

                    final ChatThreadEntity entity;

                    if (response.getId() != null
                        && response.getMessages() != null) {
                        entity = new ChatThreadEntity(
                            response.getId(),
                            response.getMessages()
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
    public void sendMessage(String content, String workmateId) {
        if (firebaseAuth.getUid() != null) {
            ChatMessageResponse chatMessageResponse = new ChatMessageResponse(content, firebaseAuth.getUid(), new Timestamp(new Date()));
            firestoreDb.collection("chats")
                .document(getChatId(workmateId))
                .update("messages", FieldValue.arrayUnion(chatMessageResponse))
                .addOnCompleteListener(task -> Log.d(TAG, "Message sent to " + workmateId))
                .addOnFailureListener(e -> Log.d(TAG, "Message could not be sent: " + e.getMessage()));
        }
    }
}
