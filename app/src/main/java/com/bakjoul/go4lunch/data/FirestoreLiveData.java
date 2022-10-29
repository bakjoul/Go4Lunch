package com.bakjoul.go4lunch.data;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreLiveData<T> extends LiveData<T> {

   private static final String TAG = "FirestoreLiveData";

   private ListenerRegistration registration;

   private final CollectionReference collectionReference;
   private final Class<?> clazz;

   public FirestoreLiveData(CollectionReference collectionReference, Class<?> clazz) {
      this.collectionReference = collectionReference;
      this.clazz = clazz;
   }

   EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
         if (error != null) {
            Log.i(TAG, "Listen failed", error);
            return;
         }


         if (querySnapshot != null && !querySnapshot.isEmpty()) {
            List<T> itemList = new ArrayList<>();
            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
               T item = (T) snapshot.toObject(clazz);
               itemList.add(item);
               Log.i(TAG, "Snapshot is " + snapshot.getId());
            }
            setValue((T) itemList);
         }
      }
   };

   @Override
   protected void onActive() {
      super.onActive();
      registration = collectionReference.addSnapshotListener(eventListener);
   }

   @Override
   protected void onInactive() {
      super.onInactive();
      if (!hasActiveObservers()) {
         registration.remove();
         registration = null;
      }
   }
}
