package com.bakjoul.go4lunch.domain.location;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import javax.inject.Inject;

public class PermissionUtil {

    @Inject
    public PermissionUtil() {
    }

    public boolean checkSelfPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
