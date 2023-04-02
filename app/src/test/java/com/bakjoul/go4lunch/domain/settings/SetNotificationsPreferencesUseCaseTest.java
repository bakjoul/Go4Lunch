package com.bakjoul.go4lunch.domain.settings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class SetNotificationsPreferencesUseCaseTest {

    private static final LocalDateTime TODAY = LocalDateTime.now();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final SettingsRepository settingsRepository = Mockito.mock(SettingsRepository.class);

    private final WorkManager workManager = Mockito.mock(WorkManager.class);

    private final Clock beforeNoonClock = Clock.fixed(
        ZonedDateTime.of(TODAY.getYear(), TODAY.getMonthValue(), TODAY.getDayOfMonth(), 11, 0, 0, 0, ZoneOffset.UTC).toInstant(),
        ZoneOffset.UTC
    );
    private final Clock afterNoonClock = Clock.fixed(
        ZonedDateTime.of(TODAY.getYear(), TODAY.getMonthValue(), TODAY.getDayOfMonth(), 13, 0, 0, 0, ZoneOffset.UTC).toInstant(),
        ZoneOffset.UTC
    );

    private SetNotificationsPreferencesUseCase setNotificationsPreferencesUseCase;

    @Test
    public void invoke_should_enable_notifications_before_noon() {
        // Given
        initUseCase(beforeNoonClock);

        // When
        setNotificationsPreferencesUseCase.invoke(true);

        // Then
        Mockito.verify(workManager).enqueueUniquePeriodicWork(
            eq("NOTIFICATION_WORK_REQUEST"),
            eq(ExistingPeriodicWorkPolicy.KEEP),
            any(PeriodicWorkRequest.class)
        );
        Mockito.verify(settingsRepository).setNotification(true);
        Mockito.verifyNoMoreInteractions(workManager, settingsRepository);
    }

    @Test
    public void invoke_should_enable_notifications_after_noon() {
        // Given
        initUseCase(afterNoonClock);

        // When
        setNotificationsPreferencesUseCase.invoke(true);

        // Then
        Mockito.verify(workManager).enqueueUniquePeriodicWork(
            eq("NOTIFICATION_WORK_REQUEST"),
            eq(ExistingPeriodicWorkPolicy.KEEP),
            any(PeriodicWorkRequest.class)
        );
        Mockito.verify(settingsRepository).setNotification(true);
        Mockito.verifyNoMoreInteractions(workManager, settingsRepository);
    }

    @Test
    public void invoke_should_disable_notifications() {
        // Given
        initUseCase(Clock.systemDefaultZone());

        // When
        setNotificationsPreferencesUseCase.invoke(false);

        // Then
        Mockito.verify(workManager).cancelUniqueWork("NOTIFICATION_WORK_REQUEST");
        Mockito.verify(settingsRepository).setNotification(false);
        Mockito.verifyNoMoreInteractions(workManager, settingsRepository);
    }

    // region IN
    private void initUseCase(Clock clock) {
        setNotificationsPreferencesUseCase = new SetNotificationsPreferencesUseCase(settingsRepository, workManager, clock);
    }
    // endregion IN
}
