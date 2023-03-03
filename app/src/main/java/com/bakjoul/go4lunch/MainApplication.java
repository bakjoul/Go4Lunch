package com.bakjoul.go4lunch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bakjoul.go4lunch.worker.NotificationWorker;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application implements Configuration.Provider {

    private static final String TAG = "NOTIFICATION_WORK_REQUEST";

    @Inject
    HiltWorkerFactory workerFactory;

    @Inject
    WorkManager workManager;

    @Inject
    Clock clock;

    @Override
    public void onCreate() {
        super.onCreate();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
            NotificationWorker.class,
            1,
            TimeUnit.DAYS
        )
            .setInitialDelay(getDelayFromLunchTime().toMillis(), TimeUnit.MILLISECONDS)
            .build();

        workManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }

    @NonNull
    private Duration getDelayFromLunchTime() {
        Duration delay = Duration.between(LocalTime.now(clock), LocalTime.of(12, 0));

        if (delay.isNegative()) {
            delay = delay.plusDays(1);
        }

        return delay;
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build();
    }
}
