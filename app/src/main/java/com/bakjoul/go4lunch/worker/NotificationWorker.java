package com.bakjoul.go4lunch.worker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final int NOTIFICATION_ID = 1;

    @AssistedInject
    public NotificationWorker(
        @Assisted Context context,
        @Assisted WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hot_bowl)
            .setContentTitle("Title")
            .setContentText("Text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                new NotificationChannel(
                    CHANNEL_ID,
                    getApplicationContext().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ));
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        return Result.success();
    }
}
