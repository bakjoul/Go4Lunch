package com.bakjoul.go4lunch.data.autocomplete.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class StructuredFormattingResponse {
    @SerializedName("main_text")
    @Expose
    private final String mainText;

    @SerializedName("main_text_matched_substrings")
    @Expose
    private final List<MatchedSubstringResponse> mainTextMatchedSubstrings;

    @SerializedName("secondary_text")
    @Expose
    private final String secondaryText;

    public StructuredFormattingResponse(String mainText, List<MatchedSubstringResponse> mainTextMatchedSubstrings, String secondaryText) {
        this.mainText = mainText;
        this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
        this.secondaryText = secondaryText;
    }

    public String getMainText() {
        return mainText;
    }

    public List<MatchedSubstringResponse> getMainTextMatchedSubstrings() {
        return mainTextMatchedSubstrings;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructuredFormattingResponse that = (StructuredFormattingResponse) o;
        return Objects.equals(mainText, that.mainText) && Objects.equals(mainTextMatchedSubstrings, that.mainTextMatchedSubstrings) && Objects.equals(secondaryText, that.secondaryText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainText, mainTextMatchedSubstrings, secondaryText);
    }

    @NonNull
    @Override
    public String toString() {
        return "StructuredFormattingResponse{" +
            "mainText='" + mainText + '\'' +
            ", mainTextMatchedSubstrings=" + mainTextMatchedSubstrings +
            ", secondaryText='" + secondaryText + '\'' +
            '}';
    }
}
