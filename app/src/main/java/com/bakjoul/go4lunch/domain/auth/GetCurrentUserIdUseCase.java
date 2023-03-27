package com.bakjoul.go4lunch.domain.auth;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GetCurrentUserIdUseCase {

    @NonNull
    private final AuthRepository authRepository;

    @Inject
    public GetCurrentUserIdUseCase(@NonNull AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public String invoke() {
        if (authRepository.getCurrentUser() != null) {
            return authRepository.getCurrentUser().getId();
        }
        return null;
    }
}
