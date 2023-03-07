package com.bakjoul.go4lunch.domain.settings;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bakjoul.go4lunch.worker.NotificationWorker;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class SetNotificationsPreferencesUseCase {

    private static final String WORK_NAME = "NOTIFICATION_WORK_REQUEST";

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final WorkManager workManager;

    @NonNull
    private final Clock clock;

    @Inject
    public SetNotificationsPreferencesUseCase(
        @NonNull SettingsRepository settingsRepository,
        @NonNull WorkManager workManager,
        @NonNull Clock clock
    ) {
        this.settingsRepository = settingsRepository;
        this.workManager = workManager;
        this.clock = clock;
    }

    public void invoke(boolean areNotificationsEnabled) {
        if (areNotificationsEnabled) {
            enqueueNotificationWork();
            settingsRepository.setNotification(true);
        } else {
            workManager.cancelUniqueWork(WORK_NAME);
            settingsRepository.setNotification(false);
        }
    }

    private void enqueueNotificationWork() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
            NotificationWorker.class,
            1,
            TimeUnit.DAYS
        )
            .setInitialDelay(getDelayFromLunchTime().toMillis(), TimeUnit.MILLISECONDS)
            .build();

        workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }

    @NonNull
    private Duration getDelayFromLunchTime() {
        Duration delay = Duration.between(LocalTime.now(clock), LocalTime.of(12, 0));

        if (delay.isNegative()) {
            delay = delay.plusDays(1);
        }

        return delay;
    }
}
