package com.bakjoul.go4lunch.data.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreCollectionLiveData;
import com.bakjoul.go4lunch.data.utils.FirestoreDocumentLiveData;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;
import com.bakjoul.go4lunch.domain.chat.ChatThreadEntity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChatRepositoryImplementation implements ChatRepository {

    private static final String TAG = "ChatRepositoryImplement";

    @NonNull
    private final FirebaseFirestore firestoreDb;

    @NonNull
    private final FirebaseAuth firebaseAuth; // TODO Bakjoul not authorized ! :D

    @Inject
    public ChatRepositoryImplementation(
        @NonNull FirebaseFirestore firestoreDb,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        this.firestoreDb = firestoreDb;
        this.firebaseAuth = firebaseAuth;
    }

    // TODO Bakjoul supprimable!
    @Override
    public void createConversation(String workmateId) {
        String chatId = getChatId(firebaseAuth.getUid(), workmateId);

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
    private String getChatId(String sender, String receiver) {
        if (sender != null
            && sender.compareTo(receiver) < 0) {
            return sender + receiver;
        } else {
            return receiver + sender;
        }
    }

    @Override
    public LiveData<ChatThreadEntity> getMessages(String workmateId) {
        if (firebaseAuth.getUid() == null) {
            return new MutableLiveData<>();
        } else {
            return new FirestoreCollectionLiveData<ChatThreadResponse, List<ChatThreadEntity>>(
                firestoreDb.collection("chats").document(getChatId(firebaseAuth.getUid(), workmateId)).collection("chat"),
                ChatThreadResponse.class
            ) {

                @Override
                public List<ChatThreadEntity> map(ChatThreadResponse response) {
                    if (response != null && response.getId() != null && response.getMessages() != null) {
                        return new ChatThreadEntity( // TODO Bakjoul change mapping to List<>
                            response.getId(),
                            response.getMessages()
                        );
                    } else {
                        return null;
                    }
                }
            };

        }
    }

    @Override
    public void sendMessage(String sender, String receiver, String content) {
        if (sender != null && receiver != null) {
            ChatMessageResponse chatMessageResponse = new ChatMessageResponse(content, firebaseAuth.getUid(), new Timestamp(new Date()));
            firestoreDb.collection("chats")
                .document(getChatId(sender, receiver))
                .collection("chat")
                .document()
                .set(chatMessageResponse)
                //.update("messages", FieldValue.arrayUnion(chatMessageResponse))
                .addOnCompleteListener(task -> {
                    if (task.getException() == null) {
                        Log.d(TAG, "Message sent to " + receiver);
                    } else {
                         Log.d(TAG, "Message could not be sent: " + task.getException().getMessage());
                    }
                });
        }
    }
}
