package com.bakjoul.go4lunch.ui.utils;

import android.content.Context;

import androidx.annotation.NonNull;

public class DensityUtil {
   public static int dip2px(@NonNull Context context, float dpValue) {
      final float scale = context.getResources().getDisplayMetrics().density;
      return (int) (dpValue * scale + 0.5f);
   }

   public static int px2dip(@NonNull Context context, float pxValue) {
      final float scale = context.getResources().getDisplayMetrics().density;
      return (int) (pxValue / scale + 0.5f);
   }
}
