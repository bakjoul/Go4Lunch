package com.bakjoul.go4lunch.ui.details;

import android.app.Application;
import android.os.Build;

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
import com.bakjoul.go4lunch.data.details.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.data.details.RestaurantDetailsResponse;
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PeriodResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.workmates.WorkmateRepository;
import com.bakjoul.go4lunch.ui.utils.DateTimeProvider;
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

@HiltViewModel
public class DetailsViewModel extends ViewModel {

   private static final String KEY = "restaurantId";

   @NonNull
   private final Application application;

   private final String restaurantId;

   @NonNull
   private final WorkmateRepository workmateRepository;

   @NonNull
   private final RestaurantImageMapper restaurantImageMapper;

   @NonNull
   private final DateTimeProvider dateTimeProvider;

   private final LiveData<DetailsViewState> detailsViewStateLiveData;

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Inject
   public DetailsViewModel(
       @NonNull Application application,
       @NonNull RestaurantDetailsRepository restaurantDetailsRepository,
       @NonNull SavedStateHandle savedStateHandle,
       @NonNull WorkmateRepository workmateRepository,
       @NonNull RestaurantImageMapper restaurantImageMapper,
       @NonNull DateTimeProvider dateTimeProvider) {

      this.application = application;
      restaurantId = savedStateHandle.get(KEY);
      this.workmateRepository = workmateRepository;
      this.restaurantImageMapper = restaurantImageMapper;
      this.dateTimeProvider = dateTimeProvider;

      if (restaurantId != null) {
         detailsViewStateLiveData = Transformations.switchMap(
             restaurantDetailsRepository.getDetailsResponse(
                 restaurantId,
                 BuildConfig.MAPS_API_KEY
             ),
             response -> {
                if (response != null) {
                   return new MutableLiveData<>(
                       map(response)
                   );
                } else {
                   return null;
                }
             }
         );
      } else {
         detailsViewStateLiveData = getErrorDetailsViewState();
      }

   }

   public LiveData<DetailsViewState> getDetailsViewStateLiveData() {
      return detailsViewStateLiveData;
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
          isLiked(),
          isChosen(),
          false,
          new ArrayList<>()
      );
   }

   private boolean isLiked() {
      List<String> likedRestaurantsId = workmateRepository.getCurrentUserData().getLikedRestaurantsIds();
      if (likedRestaurantsId != null) {
         for (String id : likedRestaurantsId) {
            if (id.equals(restaurantId)) {
               return true;
            }
         }
      }
      return false;
   }

   private boolean isChosen() {
      String chosenRestaurantId = workmateRepository.getCurrentUserData().getChosenRestaurantId();
      return chosenRestaurantId != null && chosenRestaurantId.equals(restaurantId);
   }

   @NonNull
   private MutableLiveData<DetailsViewState> getErrorDetailsViewState() {
      return new MutableLiveData<>(
          new DetailsViewState(
              null,
              null,
              application.getString(R.string.details_error_viewstate),
              0,
              false,
              null,
              null,
              null,
              null,
              false,
              false,
              false,
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

   @RequiresApi(api = Build.VERSION_CODES.O)
   @NonNull
   private String getOpeningStatus(OpeningHoursResponse response) {
      StringBuilder status;
      if (response != null) {
         LocalDateTime now = dateTimeProvider.getNow();
         Locale locale = Locale.getDefault();
         DateTimeFormatter apiTimeFormatter = DateTimeFormatter.ofPattern("HHmm", Locale.getDefault());
         int dayOfWeek;
         // Make days of week between API and Java method match
         if (DayOfWeek.from(now).getValue() == 7) {
            dayOfWeek = 0;
         } else {
            dayOfWeek = DayOfWeek.from(now).getValue();
         }

         if (response.getOpenNow()) {
            status = new StringBuilder(application.getString(R.string.restaurant_is_open));

            if (response.getPeriods() != null) {
               for (PeriodResponse p : response.getPeriods()) {
                  if (p.getOpen().getDay() != null && dayOfWeek == p.getOpen().getDay()) {
                     // Check if closing time has not passed
                     if (Long.parseLong(now.format(apiTimeFormatter)) < Long.parseLong(p.getClose().getTime()))
                        status
                            .append(application.getString(R.string.details_opened_until))
                            .append(p.getClose().getTime(), 0, 2)
                            .append(application.getString(R.string.details_time_separator))
                            .append(p.getClose().getTime(), 2, 4);
                     break;
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
                  orderedDays.add(dayToAdd);
                  dayToAdd++;
                  if (dayToAdd == 7) {
                     dayToAdd = 0;
                  }
               }
               orderedDays.add(dayToAdd);

               boolean nextOpeningFound = false;
               int daysUntilNextOpening = -1;
               // Check opening periods from today included until next 7 days
               for (int i = 0; i < orderedDays.size(); i++) {
                  daysUntilNextOpening++;
                  for (PeriodResponse p : response.getPeriods()) {
                     // If current day and day of period match
                     if (Objects.equals(orderedDays.get(i), p.getOpen().getDay())) {
                        // If it's today, ensures that the closing time has not passed
                        if (i != orderedDays.size() - 1 && Objects.equals(orderedDays.get(i), dayOfWeek) && Long.parseLong(p.getClose().getTime()) < Long.parseLong(now.format(apiTimeFormatter))) {
                           // If it has, skip to next period
                           continue;
                        }
                        nextOpeningFound = true;
                        // Add the days until the next opening to today's date to know the next opening day
                        String nextOpeningDay = now.plusDays(daysUntilNextOpening).getDayOfWeek().getDisplayName(TextStyle.SHORT, locale);
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

   public void onRestaurantUnselected() {
      workmateRepository.setChosenRestaurant(null);
   }

   public void onRestaurantSelected(String restaurantId) {
      workmateRepository.setChosenRestaurant(restaurantId);
   }

   public void onLikeButtonClicked(String restaurantId) {
      List<String> likedRestaurants = workmateRepository.getCurrentUserData().getLikedRestaurantsIds();
      if (likedRestaurants != null && !likedRestaurants.isEmpty()) {
         for (String id : likedRestaurants) {
            if (id.equals(restaurantId)) {
               workmateRepository.removeLikedRestaurant(restaurantId);
               return;
            }
         }
      }
      workmateRepository.addLikeRestaurant(restaurantId);
   }
}
