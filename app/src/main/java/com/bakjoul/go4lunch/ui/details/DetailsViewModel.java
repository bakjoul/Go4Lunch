package com.bakjoul.go4lunch.ui.details;

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
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PeriodResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.model.RestaurantDetailsResponse;
import com.bakjoul.go4lunch.data.repository.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetailsViewModel extends ViewModel {

   private final LiveData<DetailsViewState> detailsViewState;

   @NonNull
   private final RestaurantImageMapper restaurantImageMapper;

   @RequiresApi(api = Build.VERSION_CODES.O)
   @Inject
   public DetailsViewModel(
       @NonNull RestaurantDetailsRepository restaurantDetailsRepository,
       @NonNull SavedStateHandle savedStateHandle,
       @NonNull RestaurantImageMapper restaurantImageMapper) {

      String restaurantId = savedStateHandle.get("restaurantId");
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
              "An error occured",
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
         LocalDate localDate = LocalDate.now();
         int dayOfWeek = DayOfWeek.from(localDate).getValue() - 1;

         if (response.getOpenNow()) {
            status = new StringBuilder("Ouvert");

            if (response.getPeriods() != null) {
               for (PeriodResponse p : response.getPeriods()) {
                  if (p.getOpen().getDay() != null && dayOfWeek == p.getOpen().getDay()) {
                     status
                         .append(" jusqu'à ")
                         .append(p.getClose().getTime(), 0, 2)
                         .append("h")
                         .append(p.getClose().getTime(), 2, 4);
                  }
               }
            }

         } else {
            status = new StringBuilder("Fermé");

/*            if (response.getPeriods() != null) {
               for (PeriodResponse p : response.getPeriods()) {
                  if (p.getOpen().getDay() != null && p.getOpen().getDay() > dayOfWeek) {
                     Log.d("test", "getOpeningStatus: jour ouverture suivant");
                     status
                         .append("⋅ Ouvre à ")
                         .append(p.getOpen().getTime(), 0, 2)
                         .append("h")
                         .append(p.getOpen().getTime(),2, 4);
                     break;
                  }
               }
            }*/
         }
      } else {
         status = new StringBuilder("Information non disponible");
      }
      return status.toString();
   }
}
