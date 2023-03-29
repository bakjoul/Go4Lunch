package com.bakjoul.go4lunch.data.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bakjoul.go4lunch.domain.auth.AuthRepository;
import com.bakjoul.go4lunch.domain.auth.LoggedUserEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    public LoggedUserEntity getCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            final String username = firebaseUser.getDisplayName();
            final String email = firebaseUser.getEmail();
            final String photoUrl;

            if (firebaseUser.getPhotoUrl() != null) {
                photoUrl = firebaseUser.getPhotoUrl().toString();
            } else {
                photoUrl = null;
            }

            if (username != null && email != null) {
                return new LoggedUserEntity(
                    firebaseUser.getUid(),
                    username,
                    email,
                    photoUrl
                );
            }
        }
        return null;
    }
}
