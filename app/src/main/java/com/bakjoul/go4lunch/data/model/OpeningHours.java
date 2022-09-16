package com.bakjoul.go4lunch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
}
