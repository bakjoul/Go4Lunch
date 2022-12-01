package com.bakjoul.go4lunch.data.autocomplete.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class PredictionResponse {
    @SerializedName("description")
    @Expose
    private final String description;

    @SerializedName("matched_substrings")
    @Expose
    private final List<MatchedSubstringResponse> matchedSubstrings;

    @SerializedName("place_id")
    @Expose
    private final String placeId;

    @SerializedName("reference")
    @Expose
    private final String reference;

    @SerializedName("structured_formatting")
    @Expose
    private final StructuredFormattingResponse structuredFormatting;

    @SerializedName("terms")
    @Expose
    private final List<TermResponse> terms;

    @SerializedName("types")
    @Expose
    private final List<String> types;

    public PredictionResponse(String description, List<MatchedSubstringResponse> matchedSubstrings, String placeId, String reference, StructuredFormattingResponse structuredFormatting, List<TermResponse> terms, List<String> types) {
        this.description = description;
        this.matchedSubstrings = matchedSubstrings;
        this.placeId = placeId;
        this.reference = reference;
        this.structuredFormatting = structuredFormatting;
        this.terms = terms;
        this.types = types;
    }

    public String getDescription() {
        return description;
    }

    public List<MatchedSubstringResponse> getMatchedSubstrings() {
        return matchedSubstrings;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getReference() {
        return reference;
    }

    public StructuredFormattingResponse getStructuredFormatting() {
        return structuredFormatting;
    }

    public List<TermResponse> getTerms() {
        return terms;
    }

    public List<String> getTypes() {
        return types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredictionResponse that = (PredictionResponse) o;
        return Objects.equals(description, that.description) && Objects.equals(matchedSubstrings, that.matchedSubstrings) && Objects.equals(placeId, that.placeId) && Objects.equals(reference, that.reference) && Objects.equals(structuredFormatting, that.structuredFormatting) && Objects.equals(terms, that.terms) && Objects.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, matchedSubstrings, placeId, reference, structuredFormatting, terms, types);
    }

    @NonNull
    @Override
    public String toString() {
        return "PredictionResponse{" +
            "description='" + description + '\'' +
            ", matchedSubstrings=" + matchedSubstrings +
            ", placeId='" + placeId + '\'' +
            ", reference='" + reference + '\'' +
            ", structuredFormatting=" + structuredFormatting +
            ", terms=" + terms +
            ", types=" + types +
            '}';
    }
}
