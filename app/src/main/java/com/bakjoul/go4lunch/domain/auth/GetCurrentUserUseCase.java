package com.bakjoul.go4lunch.domain.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GetCurrentUserUseCase {

    @NonNull
    private final AuthRepository authRepository;

    @Inject
    public GetCurrentUserUseCase(@NonNull AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Nullable
    public LoggedUserEntity invoke() {
        return authRepository.getCurrentUser();
    }
}
