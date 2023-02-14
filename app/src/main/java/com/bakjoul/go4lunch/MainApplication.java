package com.bakjoul.go4lunch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
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

    @Inject
    HiltWorkerFactory workerFactory;

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
            //.setInitialDelay(getDelayFromLunchTime(), TimeUnit.MILLISECONDS)
            .build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }

    private long getDelayFromLunchTime() {
        Duration delay = Duration.between(LocalTime.now(clock), LocalTime.of(17, 4));

        if (delay.isNegative()) {
            delay = delay.plusDays(1);
        }

        return delay.toMillis();
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build();
    }
}
