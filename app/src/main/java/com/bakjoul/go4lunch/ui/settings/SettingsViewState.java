package com.bakjoul.go4lunch.ui.settings;

import androidx.annotation.NonNull;

import java.util.Objects;

public class SettingsViewState {

    private final boolean isLunchReminderEnabled;

    public SettingsViewState(boolean isLunchReminderEnabled) {
        this.isLunchReminderEnabled = isLunchReminderEnabled;
    }

    public boolean isLunchReminderEnabled() {
        return isLunchReminderEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingsViewState that = (SettingsViewState) o;
        return isLunchReminderEnabled == that.isLunchReminderEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isLunchReminderEnabled);
    }

    @NonNull
    @Override
    public String toString() {
        return "SettingsViewState{" +
            "isLunchReminderEnabled=" + isLunchReminderEnabled +
            '}';
    }
}
