package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class OpenResponse {
    @SerializedName("day")
    @Expose
    private final Integer day;

    @SerializedName("time")
    @Expose
    private final String time;

    public OpenResponse(Integer day, String time) {
        this.day = day;
        this.time = time;
    }

    public Integer getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenResponse that = (OpenResponse) o;
        return Objects.equals(day, that.day) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, time);
    }

    @NonNull
    @Override
    public String toString() {
        return "OpenResponse{" +
            "day=" + day +
            ", time='" + time + '\'' +
            '}';
    }
}
