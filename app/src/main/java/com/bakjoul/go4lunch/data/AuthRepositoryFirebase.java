package com.bakjoul.go4lunch.data;

import androidx.annotation.Nullable;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;

public class AuthRepositoryFirebase implements AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepositoryFirebase(
        FirebaseAuth firebaseAuth
    ) {
        this.firebaseAuth = firebaseAuth;
    }

    @Nullable
    @Override
    public String getCurrentUserId() {
        return firebaseAuth.getCurrentUser() == null ? null : firebaseAuth.getCurrentUser().getUid();
    }
}
