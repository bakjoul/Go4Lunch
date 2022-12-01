package com.bakjoul.go4lunch.data.common_model;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.details.model.PeriodResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class OpeningHoursResponse {
    @SerializedName("open_now")
    @Expose
    private final Boolean openNow;

    @SerializedName("periods")
    @Expose
    private final List<PeriodResponse> periods;

    @SerializedName("weekday_text")
    @Expose
    private final List<String> weekdayText;

    public OpeningHoursResponse(Boolean openNow, List<PeriodResponse> periods, List<String> weekdayText) {
        this.openNow = openNow;
        this.periods = periods;
        this.weekdayText = weekdayText;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public List<PeriodResponse> getPeriods() {
        return periods;
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpeningHoursResponse that = (OpeningHoursResponse) o;
        return Objects.equals(openNow, that.openNow) && Objects.equals(periods, that.periods) && Objects.equals(weekdayText, that.weekdayText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openNow, periods, weekdayText);
    }

    @NonNull
    @Override
    public String toString() {
        return "OpeningHoursResponse{" +
            "openNow=" + openNow +
            ", periods=" + periods +
            ", weekdayText=" + weekdayText +
            '}';
    }
}
