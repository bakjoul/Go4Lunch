package com.bakjoul.go4lunch.data.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class DetailsResponse {
    @SerializedName("result")
    @Expose
    private final ResultResponse result;
    @SerializedName("status")
    @Expose
    private final String status;

    public DetailsResponse(ResultResponse result, String status) {
        this.result = result;
        this.status = status;
    }

    public ResultResponse getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailsResponse that = (DetailsResponse) o;
        return Objects.equals(result, that.result) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, status);
    }

    @NonNull
    @Override
    public String toString() {
        return "DetailsResponse{" +
            "result=" + result +
            ", status='" + status + '\'' +
            '}';
    }
}
