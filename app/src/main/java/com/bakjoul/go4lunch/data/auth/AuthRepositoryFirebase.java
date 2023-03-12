package com.bakjoul.go4lunch.data.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepositoryFirebase implements AuthRepository {

    private final FirebaseAuth firebaseAuth;

    @Inject
    public AuthRepositoryFirebase(@NonNull FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Nullable
    @Override
    public String getCurrentUserId() {
        return firebaseAuth.getCurrentUser() == null ? null : firebaseAuth.getCurrentUser().getUid();
    }
}
