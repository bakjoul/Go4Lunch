package com.bakjoul.go4lunch.ui.utils;

import androidx.annotation.NonNull;

import javax.inject.Inject;

public class RandomUUIDUtil {

    @Inject
    public RandomUUIDUtil() {
    }

    @NonNull
    public String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }
}
