package com.bakjoul.go4lunch.ui.details;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.BuildConfig;
import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.details.RestaurantDetailsResponse;
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PeriodResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.domain.details.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.domain.user.UserGoingToRestaurantEntity;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
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
    private final UserRepository userRepository;

    @NonNull
    private final RestaurantImageMapper restaurantImageMapper;

    @NonNull
    private final Clock clock;

    private final MediatorLiveData<DetailsViewState> detailsViewStateMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public DetailsViewModel(
        @NonNull Application application,
        @NonNull RestaurantDetailsRepository restaurantDetailsRepository,
        @NonNull SavedStateHandle savedStateHandle,
        @NonNull UserRepository userRepository,
        @NonNull WorkmateRepositoryImplementation workmateRepositoryImplementation,
        @NonNull RestaurantImageMapper restaurantImageMapper,
        @NonNull Clock clock
    ) {
        this.application = application;
        this.userRepository = userRepository;
        this.restaurantImageMapper = restaurantImageMapper;
        this.clock = clock;

        restaurantId = savedStateHandle.get(KEY);

        final LiveData<DetailsResponse> detailsResponseLiveData;
        final LiveData<List<UserGoingToRestaurantEntity>> workmatesLiveData;
        final LiveData<UserGoingToRestaurantEntity> chosenRestaurantLiveData;
        final LiveData<Collection<String>> favoriteRestaurantsLiveData;

        if (restaurantId != null) {
            detailsResponseLiveData = restaurantDetailsRepository.getDetailsResponse(restaurantId, BuildConfig.MAPS_API_KEY);
            workmatesLiveData = workmateRepositoryImplementation.getWorkmatesGoingToRestaurantIdLiveData(restaurantId);
            chosenRestaurantLiveData = userRepository.getChosenRestaurantLiveData();
            favoriteRestaurantsLiveData = userRepository.getFavoritesRestaurantsLiveData();

            detailsViewStateMediatorLiveData.addSource(detailsResponseLiveData, detailsResponse ->
                combine(detailsResponse, workmatesLiveData.getValue(), chosenRestaurantLiveData.getValue(), favoriteRestaurantsLiveData.getValue())
            );
            detailsViewStateMediatorLiveData.addSource(workmatesLiveData, workmateEntities ->
                combine(detailsResponseLiveData.getValue(), workmateEntities, chosenRestaurantLiveData.getValue(), favoriteRestaurantsLiveData.getValue())
            );
            detailsViewStateMediatorLiveData.addSource(chosenRestaurantLiveData, chosenRestaurant ->
                combine(detailsResponseLiveData.getValue(), workmatesLiveData.getValue(), chosenRestaurant, favoriteRestaurantsLiveData.getValue())
            );
            detailsViewStateMediatorLiveData.addSource(favoriteRestaurantsLiveData, favoriteRestaurants ->
                combine(detailsResponseLiveData.getValue(), workmatesLiveData.getValue(), chosenRestaurantLiveData.getValue(), favoriteRestaurants)
            );
        } else {
            detailsViewStateMediatorLiveData.setValue(getErrorDetailsViewState());
        }
    }

    private void combine(
        @Nullable DetailsResponse response,
        @Nullable List<UserGoingToRestaurantEntity> workmates,
        @Nullable UserGoingToRestaurantEntity chosenRestaurant,
        @Nullable Collection<String> favoriteRestaurants
    ) {
        if (response == null || workmates == null || favoriteRestaurants == null) {
            return;
        }

        boolean isRestaurantChosen = false;
        boolean isRestaurantFavorite = false;

        // Checks if current restaurant is chosen by user
        if (chosenRestaurant != null && chosenRestaurant.getChosenRestaurantId().equals(restaurantId)) {
            isRestaurantChosen = true;
        }

        // Checks if current restaurant is in user's favorites
        if (favoriteRestaurants.contains(restaurantId)) {
            isRestaurantFavorite = true;
        }

        detailsViewStateMediatorLiveData.setValue(
            mapResponse(
                response,
                workmates,
                isRestaurantChosen,
                isRestaurantFavorite
            )
        );

    }

    public LiveData<DetailsViewState> getDetailsViewStateMediatorLiveData() {
        return detailsViewStateMediatorLiveData;
    }

    @NonNull
    private DetailsViewState mapResponse(
        @NonNull DetailsResponse response,
        @NonNull List<UserGoingToRestaurantEntity> workmates,
        boolean isRestaurantChosen,
        boolean isRestaurantFavorite
    ) {
        RestaurantDetailsResponse r = response.getResult();
        List<DetailsItemViewState> workmatesList = mapWorkmates(workmates);
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
            isRestaurantChosen,
            isRestaurantFavorite,
            false,
            workmatesList,
            workmatesList.isEmpty());
    }

    @NonNull
    private List<DetailsItemViewState> mapWorkmates(@NonNull List<UserGoingToRestaurantEntity> workmates) {
        List<DetailsItemViewState> workmatesList = new ArrayList<>();
        for (UserGoingToRestaurantEntity entity : workmates) {
            DetailsItemViewState workmateItem = new DetailsItemViewState(
                entity.getId(),
                entity.getUsername(),
                entity.getPhotoUrl(),
                entity.getUsername() + application.getString(R.string.details_text_joining)
            );
            workmatesList.add(workmateItem);
        }
        return workmatesList;
    }

    @NonNull
    private DetailsViewState getErrorDetailsViewState() {
        return new DetailsViewState(
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
            null,
            true);
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

    @SuppressLint("NewApi")
    @NonNull
    private String getOpeningStatus(OpeningHoursResponse response) {
        StringBuilder status;
        if (response != null) {
            LocalDateTime now = LocalDateTime.now(clock);
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

    public void onRestaurantChoosed(String restaurantId, String restaurantName) {
        userRepository.chooseRestaurant(restaurantId, restaurantName);
    }

    public void onRestaurantUnchoosed() {
        userRepository.unchooseRestaurant();
    }

    public void onFavoriteButtonClicked(String restaurantId, String restaurantName) {
        userRepository.addRestaurantToFavorites(restaurantId, restaurantName);
    }

    public void onUnfavoriteButtonClicked(String restaurantId) {
        userRepository.removeRestaurantFromFavorites(restaurantId);
    }
}
