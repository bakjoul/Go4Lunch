package com.bakjoul.go4lunch.data.autocomplete.model;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Objects;

public class AutocompleteQuery {
    @NonNull
    private final String userInput;
    @NonNull
    private final BigDecimal latitude;
    @NonNull
    private final BigDecimal longitude;

    public AutocompleteQuery(@NonNull String userInput, @NonNull BigDecimal latitude, @NonNull BigDecimal longitude) {
        this.userInput = userInput;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutocompleteQuery that = (AutocompleteQuery) o;
        return userInput.equals(that.userInput) && latitude.equals(that.latitude) && longitude.equals(that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userInput, latitude, longitude);
    }
}
