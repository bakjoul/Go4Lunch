package com.bakjoul.go4lunch.ui.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

import javax.inject.Inject;

public class DateTimeProvider {

   @Inject
   public DateTimeProvider() {
   }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public LocalDateTime getNow() {
      return LocalDateTime.now();
   }
}
