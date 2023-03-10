package com.bakjoul.go4lunch.data.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bakjoul.go4lunch.data.utils.FirestoreQueryLiveData;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;
import com.bakjoul.go4lunch.domain.chat.ChatThreadEntity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public LiveData<List<ChatThreadEntity>> getMessages(String workmateId) {
        if (firebaseAuth.getUid() == null) {
            return new MutableLiveData<>();
        } else {
            return new FirestoreQueryLiveData<ChatThreadResponse, ChatThreadEntity>(
                firestoreDb.collection("chats")
                    .whereArrayContains("participants", firebaseAuth.getUid())
                    .whereArrayContains("participants", workmateId),
                ChatThreadResponse.class
            ) {

                @Override
                public ChatThreadEntity map(ChatThreadResponse response) {
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
            ChatMessage chatMessage = new ChatMessage(content, firebaseAuth.getUid(), new Timestamp(new Date()));
            firestoreDb.collection("users")
                .document(firebaseAuth.getUid())
                .collection("chats")
                .document(workmateId)
                .update("messages", FieldValue.arrayUnion(chatMessage))
                .addOnCompleteListener(task -> Log.d(TAG, "Message sent to " + workmateId))
                .addOnFailureListener(e -> Log.d(TAG, "Message could not be sent: " + e.getMessage()));
        }
    }
}
