package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class PeriodResponse {
    @SerializedName("close")
    @Expose
    private final CloseResponse closeResponse;

    @SerializedName("open")
    @Expose
    private final OpenResponse openResponse;

    public PeriodResponse(CloseResponse closeResponse, OpenResponse openResponse) {
        this.closeResponse = closeResponse;
        this.openResponse = openResponse;
    }

    public CloseResponse getClose() {
        return closeResponse;
    }

    public OpenResponse getOpen() {
        return openResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeriodResponse that = (PeriodResponse) o;
        return Objects.equals(closeResponse, that.closeResponse) && Objects.equals(openResponse, that.openResponse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(closeResponse, openResponse);
    }

    @NonNull
    @Override
    public String toString() {
        return "PeriodResponse{" +
            "close=" + closeResponse +
            ", open=" + openResponse +
            '}';
    }
}
