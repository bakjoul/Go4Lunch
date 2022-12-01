package com.bakjoul.go4lunch.data.autocomplete.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class MatchedSubstringResponse {
    @SerializedName("length")
    @Expose
    private final Integer length;

    @SerializedName("offset")
    @Expose
    private final Integer offset;

    public MatchedSubstringResponse(Integer length, Integer offset) {
        this.length = length;
        this.offset = offset;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchedSubstringResponse that = (MatchedSubstringResponse) o;
        return Objects.equals(length, that.length) && Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, offset);
    }

    @NonNull
    @Override
    public String toString() {
        return "MatchedSubstringResponse{" +
            "length=" + length +
            ", offset=" + offset +
            '}';
    }
}
