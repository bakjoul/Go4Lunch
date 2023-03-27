package com.bakjoul.go4lunch.domain.auth;

import androidx.annotation.Nullable;

public interface AuthRepository {

    @Nullable
    LoggedUserEntity getCurrentUser();
}
