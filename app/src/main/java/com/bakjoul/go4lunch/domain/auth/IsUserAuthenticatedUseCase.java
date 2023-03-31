package com.bakjoul.go4lunch.domain.auth;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class IsUserAuthenticatedUseCase {

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    public IsUserAuthenticatedUseCase(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public boolean invoke() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
