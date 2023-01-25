package com.bakjoul.go4lunch.domain.dispatcher;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class GetAuthUseCase {

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    public GetAuthUseCase(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
