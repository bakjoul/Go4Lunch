package com.bakjoul.go4lunch.domain.auth;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GetCurrentFirebaseUserUseCase {

    @NonNull
    private final FirebaseAuth firebaseAuth;

    @Inject
    public GetCurrentFirebaseUserUseCase(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseUser invoke() {
        return firebaseAuth.getCurrentUser();
    }
}
