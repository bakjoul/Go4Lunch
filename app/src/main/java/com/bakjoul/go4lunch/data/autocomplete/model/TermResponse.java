package com.bakjoul.go4lunch.data.autocomplete.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class TermResponse {
    @SerializedName("offset")
    @Expose
    private final Integer offset;

    @SerializedName("value")
    @Expose
    private final String value;

    public TermResponse(Integer offset, String value) {
        this.offset = offset;
        this.value = value;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermResponse that = (TermResponse) o;
        return Objects.equals(offset, that.offset) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "TermResponse{" +
            "offset=" + offset +
            ", value='" + value + '\'' +
            '}';
    }
}
