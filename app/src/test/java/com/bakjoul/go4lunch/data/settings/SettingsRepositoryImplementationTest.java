package com.bakjoul.go4lunch.data.settings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class SettingsRepositoryImplementationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final Context context = Mockito.mock(Context.class);

    private static final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);

    private SettingsRepositoryImplementation settingsRepositoryImplementation;

    @Before
    public void setUp() {
        doReturn(sharedPreferences).when(context).getSharedPreferences("settings", Context.MODE_PRIVATE);

        settingsRepositoryImplementation = new SettingsRepositoryImplementation(context);
    }

    @Test
    public void setNotification_true_should_update_notification_preference_to_true() {
        // Given
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        doReturn(editor).when(sharedPreferences).edit();
        doReturn(editor).when(editor).putBoolean("lunchReminder", true);

        // When
        settingsRepositoryImplementation.setNotification(true);

        // Then
        verify(editor).putBoolean("lunchReminder", true);
        verify(editor).apply();
    }

    @Test
    public void setNotification_false_should_update_notification_preference_to_false() {
        // Given
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        doReturn(editor).when(sharedPreferences).edit();
        doReturn(editor).when(editor).putBoolean("lunchReminder", false);

        // When
        settingsRepositoryImplementation.setNotification(false);

        // Then
        verify(editor).putBoolean("lunchReminder", false);
        verify(editor).apply();
    }

    @Test
    public void areNotificationsEnabled_should_return_true_when_notifications_are_enabled() {
        // Given
        doReturn(true).when(sharedPreferences).getBoolean("lunchReminder", true);

        // When
        boolean areNotificationsEnabled = settingsRepositoryImplementation.areNotificationsEnabled();

        // Then
        assertTrue(areNotificationsEnabled);
    }

    @Test
    public void areNotificationsEnabled_should_return_false_when_notifications_are_disabled() {
        // Given
        doReturn(false).when(sharedPreferences).getBoolean("lunchReminder", true);

        // When
        boolean areNotificationsEnabled = settingsRepositoryImplementation.areNotificationsEnabled();

        // Then
        assertFalse(areNotificationsEnabled);
    }

    @Test
    public void areNotificationsEnabledLiveData_should_return_true_when_notifications_are_enabled() {
        // Given
        doReturn(true).when(sharedPreferences).getBoolean("lunchReminder", true);

        // When
        boolean areNotificationsEnabled = LiveDataTestUtil.getValueForTesting(settingsRepositoryImplementation.areNotificationsEnabledLiveData());

        // Then
        assertTrue(areNotificationsEnabled);
    }

    @Test
    public void areNotificationsEnabledLiveData_should_return_false_when_notifications_are_disabled() {
        // Given
        doReturn(false).when(sharedPreferences).getBoolean("lunchReminder", true);

        // When
        boolean areNotificationsEnabled = LiveDataTestUtil.getValueForTesting(settingsRepositoryImplementation.areNotificationsEnabledLiveData());

        // Then
        assertFalse(areNotificationsEnabled);
    }
}