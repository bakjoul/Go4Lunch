package com.bakjoul.go4lunch.ui.details;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PeriodResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.model.RestaurantDetailsResponse;
import com.bakjoul.go4lunch.data.repository.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@RequiresApi(api = Build.VERSION_CODES.O)
@HiltViewModel
public class DetailsViewModel extends ViewModel {
   
   private static final String KEY = "restaurantId";

   @NonNull
   private final Application application;

   private final LiveData<DetailsViewState> detailsViewState;

   @NonNull
   private final RestaurantImageMapper restaurantImageMapper;

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Inject
   public DetailsViewModel(
       @NonNull Application application,
       @NonNull RestaurantDetailsRepository restaurantDetailsRepository,
       @NonNull SavedStateHandle savedStateHandle,
       @NonNull RestaurantImageMapper restaurantImageMapper) {

      this.application = application;
      String restaurantId = savedStateHandle.get(KEY);
      this.restaurantImageMapper = restaurantImageMapper;

      if (restaurantId != null) {
         detailsViewState = Transformations.switchMap(
             restaurantDetailsRepository.getDetailsResponse(
                 restaurantId,
                 BuildConfig.MAPS_API_KEY
             ),
             response -> {
                if (response != null) {
                   return new MutableLiveData<>(
                       map(response)
                   );
                }
                return null;
             }
         );
      } else {
         detailsViewState = getErrorDetailsViewState();
      }

   }

   public LiveData<DetailsViewState> getDetailsViewState() {
      return detailsViewState;
   }

   @RequiresApi(api = Build.VERSION_CODES.O)
   @NonNull
   private DetailsViewState map(@NonNull DetailsResponse response) {
      RestaurantDetailsResponse r = response.getResult();
      return new DetailsViewState(
          r.getPlaceId(),
          getPhotoUrl(r.getPhotoResponses()),
          r.getName(),
          getRating(r.getRating()),
          isRatingBarVisible(r.getUserRatingsTotal()),
          r.getFormattedAddress(),
          getOpeningStatus(r.getOpeningHoursResponse()),
          r.getFormattedPhoneNumber(),
          r.getWebsite(),
          new ArrayList<>()
      );
   }

   @NonNull
   private MutableLiveData<DetailsViewState> getErrorDetailsViewState() {
      return new MutableLiveData<>(
          new DetailsViewState(
              "",
              null,
              application.getString(R.string.details_error_viewstate),
              0,
              false,
              "",
              "",
              "",
              "",
              null
          )
      );
   }

   @Nullable
   private String getPhotoUrl(List<PhotoResponse> photoResponses) {
      if (photoResponses != null) {
         String photoRef = photoResponses.get(0).getPhotoReference();
         if (photoRef != null) {
            return restaurantImageMapper.getImageUrl(photoRef, true);
         }
      }
      return null;
   }

   private float getRating(double restaurantRating) {
      return (float) Math.round(((restaurantRating * 3 / 5) / 0.5) * 0.5);
   }

   private boolean isRatingBarVisible(int userRatingsTotal) {
      return userRatingsTotal > 0;
   }

   @NonNull
   @RequiresApi(api = Build.VERSION_CODES.O)
   private String getOpeningStatus(OpeningHoursResponse response) {
      StringBuilder status;
      if (response != null) {
         LocalDateTime localDateTime = LocalDateTime.now();
         Locale locale = Locale.getDefault();
         DateTimeFormatter apiTimeFormatter = DateTimeFormatter.ofPattern("HHmm", Locale.getDefault());
         int dayOfWeek;
         // Make days of week between API and Java method match
         if (DayOfWeek.from(localDateTime).getValue() == 7) {
            dayOfWeek = 0;
         } else {
            dayOfWeek = DayOfWeek.from(localDateTime).getValue();
         }

         if (response.getOpenNow()) {
            status = new StringBuilder(application.getString(R.string.restaurant_is_open));

            if (response.getPeriods() != null) {
               for (PeriodResponse p : response.getPeriods()) {
                  if (p.getOpen().getDay() != null && dayOfWeek == p.getOpen().getDay()) {
                     status
                         .append(application.getString(R.string.details_opened_until))
                         .append(p.getClose().getTime(), 0, 2)
                         .append(application.getString(R.string.details_time_separator))
                         .append(p.getClose().getTime(), 2, 4);
                  }
               }
            }

         } else {
            status = new StringBuilder(application.getString(R.string.restaurant_is_closed));

            if (response.getPeriods() != null) {
               // Chronologically orders the 8 next days of week starting from today
               List<Integer> orderedDays = new ArrayList<>();
               int dayToAdd = dayOfWeek;
               while (!orderedDays.contains(0)
                   || !orderedDays.contains(1)
                   || !orderedDays.contains(2)
                   || !orderedDays.contains(3)
                   || !orderedDays.contains(4)
                   || !orderedDays.contains(5)
                   || !orderedDays.contains(6)
               ) {
                  if (dayToAdd > 6) {
                     dayToAdd = 0;
                  }
                  orderedDays.add(dayToAdd);
                  dayToAdd++;
               }
               if (dayToAdd > 6) {
                  dayToAdd = 0;
               }
               orderedDays.add(dayToAdd);

               boolean nextOpeningFound = false;
               // Check opening periods from today included until next 6 days
               for (Integer i : orderedDays) {
                  for (PeriodResponse p : response.getPeriods()) {
                     // If current day and day of period match
                     if (Objects.equals(i, p.getOpen().getDay())) {
                        // If it's today, check that the closing time has not passed
                        if (Objects.equals(i, orderedDays.get(0)) && Long.parseLong(p.getClose().getTime()) < Long.parseLong(localDateTime.format(apiTimeFormatter))) {
                           // If it has, skip to next period
                           continue;
                        }
                        nextOpeningFound = true;
                        // Get the index of the next opening day
                        int daysUntilNextOpening = orderedDays.indexOf(i);
                        // Add the days until the next opening to today's date to know the next opening day
                        String nextOpeningDay = localDateTime.plusDays(daysUntilNextOpening).getDayOfWeek().getDisplayName(TextStyle.SHORT, locale);
                        status
                            .append(application.getString(R.string.details_open_at))
                            .append(p.getOpen().getTime(), 0, 2)
                            .append(application.getString(R.string.details_time_separator))
                            .append(p.getOpen().getTime(), 2, 4)
                            .append(" ")
                            .append(nextOpeningDay);
                        break;
                     }
                  }
                  // Stop iterating if the next opening day was found
                  if (nextOpeningFound) {
                     break;
                  }
               }
            }
         }
      } else {
         status = new StringBuilder(application.getString(R.string.information_not_available));
      }
      return status.toString();
   }
}
