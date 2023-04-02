package com.bakjoul.go4lunch.domain.location;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class IsLocationPermissionGrantedUseCase {

    @NonNull
    private final Context context;

    @NonNull
    private final PermissionUtil permissionUtil;

    @Inject
    public IsLocationPermissionGrantedUseCase(
        @NonNull @ApplicationContext Context context,
        @NonNull PermissionUtil permissionUtil
    ) {
        this.context = context;
        this.permissionUtil = permissionUtil;
    }

    public Boolean invoke() {
        return permissionUtil.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
