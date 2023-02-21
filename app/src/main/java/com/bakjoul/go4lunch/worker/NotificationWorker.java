package com.bakjoul.go4lunch.worker;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.user.UserGoingToRestaurantResponse;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class NotificationWorker extends Worker {

    private static final String TAG = "NotificationWorker";

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final int NOTIFICATION_ID = 1;

    @NonNull
    private final FirebaseFirestore firebaseFirestore;
    @NonNull
    private final FirebaseAuth firebaseAuth;

    @AssistedInject
    public NotificationWorker(
        @Assisted Context context,
        @Assisted WorkerParameters workerParams,
        @NonNull FirebaseFirestore firebaseFirestore,
        @NonNull FirebaseAuth firebaseAuth
    ) {
        super(context, workerParams);
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {
        UserGoingToRestaurantEntity userGoingToRestaurantEntity = getUserChosenRestaurantData();

        if (userGoingToRestaurantEntity == null) {
            return Result.success();
        }

        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("restaurantId", userGoingToRestaurantEntity.getChosenRestaurantId());

        final int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, flag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hot_bowl)
            .setContentTitle(getApplicationContext().getString(R.string.notification_title))
            .setContentText(getContentText(userGoingToRestaurantEntity))
            .setStyle(new NotificationCompat.BigTextStyle().bigText(getBigText(userGoingToRestaurantEntity)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                new NotificationChannel(
                    CHANNEL_ID,
                    getApplicationContext().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            );
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        return Result.success();
    }

    @NonNull
    private String getContentText(@NonNull UserGoingToRestaurantEntity userGoingToRestaurantEntity) {
        return userGoingToRestaurantEntity.getChosenRestaurantName()
            + getApplicationContext().getString(R.string.notification_waiting_for_you)
            + userGoingToRestaurantEntity.getChosenRestaurantAddress();
    }

    @NonNull
    private String getContextDetails(@NonNull UserGoingToRestaurantEntity userGoingToRestaurantEntity) {
        StringBuilder workmatesGoingToRestaurantList = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        firebaseFirestore.collection("usersGoingToRestaurants")
            .whereEqualTo("chosenRestaurantId", userGoingToRestaurantEntity.getChosenRestaurantId())
            .addSnapshotListener((querySnapshot, error) -> {
                if (error != null) {
                    Log.i(TAG, "Listen failed", error);
                }
                if (querySnapshot != null) {
                    List<UserGoingToRestaurantResponse> responses = querySnapshot.toObjects(UserGoingToRestaurantResponse.class);
                    List<UserGoingToRestaurantEntity> entities = mapResponses(responses);
                    entities.remove(userGoingToRestaurantEntity);

                    for (int i = 0; i < entities.size(); i++) {
                        workmatesGoingToRestaurantList.append(entities.get(i).getUsername());

                        if (i + 2 < entities.size()) {
                            workmatesGoingToRestaurantList.append(", ");
                        } else if (i + 1 < entities.size()) {
                            workmatesGoingToRestaurantList.append(getApplicationContext().getString(R.string.notification_and));
                        } else if (i == entities.size()) {
                            workmatesGoingToRestaurantList.append(".");
                        }
                    }
                }

                latch.countDown();
            });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (workmatesGoingToRestaurantList.toString().isEmpty()) {
            return "";
        } else {
            return getApplicationContext().getString(R.string.notification_with) + workmatesGoingToRestaurantList;
        }
    }

    @NonNull
    private String getBigText(UserGoingToRestaurantEntity userGoingToRestaurantEntity) {
        return getContentText(userGoingToRestaurantEntity) + getContextDetails(userGoingToRestaurantEntity);
    }

    private UserGoingToRestaurantEntity getUserChosenRestaurantData() {
        UserGoingToRestaurantEntity[] entity = new UserGoingToRestaurantEntity[1];
        CountDownLatch latch = new CountDownLatch(1);

        if (firebaseAuth.getUid() != null) {
            firebaseFirestore.collection("usersGoingToRestaurants").document(firebaseAuth.getUid())
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.i(TAG, "Listen failed", error);
                    }
                    if (documentSnapshot != null) {
                        UserGoingToRestaurantResponse response = documentSnapshot.toObject(UserGoingToRestaurantResponse.class);
                        entity[0] = mapResponse(response);
                    }

                    latch.countDown();
                });

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return entity[0];
    }

    @Nullable
    private UserGoingToRestaurantEntity mapResponse(UserGoingToRestaurantResponse response) {
        final UserGoingToRestaurantEntity entity;

        if (response != null
            && response.getId() != null
            && response.getUsername() != null
            && response.getEmail() != null
            && response.getPhotoUrl() != null
            && response.getChosenRestaurantId() != null
            && response.getChosenRestaurantName() != null
            && response.getChosenRestaurantAddress() != null) {
            entity = new UserGoingToRestaurantEntity(
                response.getId(),
                response.getUsername(),
                response.getEmail(),
                response.getPhotoUrl(),
                response.getChosenRestaurantId(),
                response.getChosenRestaurantName(),
                response.getChosenRestaurantAddress()
            );
        } else {
            entity = null;
        }

        return entity;
    }

    @NonNull
    private List<UserGoingToRestaurantEntity> mapResponses(@NonNull List<UserGoingToRestaurantResponse> responses) {
        List<UserGoingToRestaurantEntity> entities = new ArrayList<>();

        for (UserGoingToRestaurantResponse response : responses) {
            if (response != null
                && response.getId() != null
                && response.getUsername() != null
                && response.getEmail() != null
                && response.getPhotoUrl() != null
                && response.getChosenRestaurantId() != null
                && response.getChosenRestaurantName() != null
                && response.getChosenRestaurantAddress() != null) {
                UserGoingToRestaurantEntity entity = new UserGoingToRestaurantEntity(
                    response.getId(),
                    response.getUsername(),
                    response.getEmail(),
                    response.getPhotoUrl(),
                    response.getChosenRestaurantId(),
                    response.getChosenRestaurantName(),
                    response.getChosenRestaurantAddress()
                );
                entities.add(entity);
            }
        }

        return entities;
    }
}
