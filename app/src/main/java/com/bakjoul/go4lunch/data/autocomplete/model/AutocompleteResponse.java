package com.bakjoul.go4lunch.data.autocomplete.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class AutocompleteResponse {
    @SerializedName("predictions")
    @Expose
    private final List<PredictionResponse> predictions;

    @SerializedName("status")
    @Expose
    private final String status;

    public AutocompleteResponse(List<PredictionResponse> predictions, String status) {
        this.predictions = predictions;
        this.status = status;
    }

    public List<PredictionResponse> getPredictions() {
        return predictions;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutocompleteResponse that = (AutocompleteResponse) o;
        return Objects.equals(predictions, that.predictions) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predictions, status);
    }

    @NonNull
    @Override
    public String toString() {
        return "AutocompleteResponse{" +
            "predictions=" + predictions +
            ", status='" + status + '\'' +
            '}';
    }
}
