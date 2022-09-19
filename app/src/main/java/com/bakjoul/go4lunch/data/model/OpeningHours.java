package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class OpeningHours {
    @SerializedName("open_now")
    @Expose
    private final Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    public OpeningHours(Boolean openNow) {
        this.openNow = openNow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpeningHours that = (OpeningHours) o;
        return Objects.equals(openNow, that.openNow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openNow);
    }

    @NonNull
    @Override
    public String toString() {
        return "OpeningHours{" +
            "openNow=" + openNow +
            '}';
    }
}
