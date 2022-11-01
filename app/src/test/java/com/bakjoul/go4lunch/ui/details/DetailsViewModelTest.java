package com.bakjoul.go4lunch.ui.details;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.details.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.data.details.RestaurantDetailsResponse;
import com.bakjoul.go4lunch.data.model.CloseResponse;
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.data.model.OpenResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PeriodResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class DetailsViewModelTest {

   // region Constants
   private static final String OPEN = "Ouvert";
   private static final String CLOSED = "Fermé";
   private static final String NOT_AVAILABLE = "Information non disponible";
   private static final String UNTIL = " jusqu'à ";
   private static final String TIME_SEPARATOR = "h";
   private static final String OPEN_AT = " ⋅ Ouvre à ";

   private static final LocalDateTime FAKE_DATE_TIME = LocalDateTime.of(2022, 10, 16, 12, 0);
   private static final LocalDateTime FAKE_DATE_TIME_2 = LocalDateTime.of(2022, 10, 17, 12, 0);

   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_1 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_1",
       "RESTAURANT_DETAILS_RESPONSE_NAME_1",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_1",
       new OpeningHoursResponse(true, null, null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_1",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_1"
   );
   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_2 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_2",
       "RESTAURANT_DETAILS_RESPONSE_NAME_2",
       0,
       0,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_2",
       null,
       null,
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_2",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_2"
   );
   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_3 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_3",
       "RESTAURANT_DETAILS_RESPONSE_NAME_3",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_3",
       new OpeningHoursResponse(true, null, null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse(null))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_3",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_3"
   );
   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_4 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_4",
       "RESTAURANT_DETAILS_RESPONSE_NAME_4",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_4",
       new OpeningHoursResponse(true, new ArrayList<>(Collections.singletonList(new PeriodResponse(new CloseResponse(0, "1500"), new OpenResponse(0, "0900")))), null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_4",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_4"
   );
   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_5 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_5",
       "RESTAURANT_DETAILS_RESPONSE_NAME_5",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_5",
       new OpeningHoursResponse(false, new ArrayList<>(Collections.singletonList(new PeriodResponse(new CloseResponse(1, "1500"), new OpenResponse(1, "1000")))), null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_5",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_5"
   );
   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_6 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_6",
       "RESTAURANT_DETAILS_RESPONSE_NAME_6",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_6",
       new OpeningHoursResponse(false, new ArrayList<>(Collections.singletonList(new PeriodResponse(new CloseResponse(0, "1100"), new OpenResponse(0, "1000")))), null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_6",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_6"
   );
   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_7 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_7",
       "RESTAURANT_DETAILS_RESPONSE_NAME_7",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_7",
       new OpeningHoursResponse(false, null, null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_7",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_7"
   );
   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE_8 = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID_8",
       "RESTAURANT_DETAILS_RESPONSE_NAME_8",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS_8",
       new OpeningHoursResponse(true, new ArrayList<>(Collections.singletonList(new PeriodResponse(new CloseResponse(null, null), new OpenResponse(null, null)))), null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER_8",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE_8"
   );
   // endregion Constants

   @Rule
   public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

   private final Application application = Mockito.mock(Application.class);
   private final RestaurantDetailsRepository restaurantDetailsRepository = Mockito.mock(RestaurantDetailsRepository.class);
   private final SavedStateHandle savedStateHandle = Mockito.mock(SavedStateHandle.class);
   private final WorkmateRepositoryImplementation workmateRepository = Mockito.mock(WorkmateRepositoryImplementation.class);
   private final RestaurantImageMapper restaurantImageMapper = Mockito.mock(RestaurantImageMapper.class);
   private final DateTimeProvider dateTimeProvider = Mockito.mock(DateTimeProvider.class);

   private final MutableLiveData<DetailsResponse> detailsResponseLiveData = new MutableLiveData<>();

   private DetailsViewModel viewModel;

   @Before
   public void setUp() {
      doReturn(OPEN).when(application).getString(R.string.restaurant_is_open);
      doReturn(CLOSED).when(application).getString(R.string.restaurant_is_closed);
      doReturn(NOT_AVAILABLE).when(application).getString(R.string.information_not_available);
      doReturn(UNTIL).when(application).getString(R.string.details_opened_until);
      doReturn(TIME_SEPARATOR).when(application).getString(R.string.details_time_separator);
      doReturn(OPEN_AT).when(application).getString(R.string.details_open_at);

      doReturn(detailsResponseLiveData).when(restaurantDetailsRepository).getDetailsResponse(anyString(), anyString());
      doReturn("fakeImageUrl").when(restaurantImageMapper).getImageUrl("fakePhotoReference", true);
      doReturn(LocalDateTime.now()).when(dateTimeProvider).getNow();
   }

   @Test
   public void nominal_case() {
      // Given
      doReturn(RESTAURANT_DETAILS_RESPONSE_1.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_1, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals(getDefaultDetailsViewState(), result);
   }

   @Test
   public void restaurantId_null_should_expose_error_viewstate() {
      // Given
      doReturn(null).when(savedStateHandle).get("restaurantId");
      initViewModel();

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals(getErrorDetailsViewState(), result);
   }

   @Test
   public void response_null_should_expose_null_viewstate() {
      doReturn(RESTAURANT_DETAILS_RESPONSE_1.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(null);

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertNull(result);
   }

   @Test
   public void photoResponse_null_should_return_null_photoUrl() {
      // Given
      doReturn(RESTAURANT_DETAILS_RESPONSE_2.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_2, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertNull(result.getPhotoUrl());
   }

   @Test
   public void photoRef_null_should_return_null_photoUrl() {
      // Given
      doReturn(FAKE_DATE_TIME_2).when(dateTimeProvider).getNow();
      doReturn(RESTAURANT_DETAILS_RESPONSE_3.getPlaceId()).when(savedStateHandle).get("restaurantId");
      doReturn(null).when(restaurantImageMapper).getImageUrl(anyString(), anyBoolean());
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_3, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertNull(result.getPhotoUrl());
   }

   @Test
   public void no_user_rating_should_return_rating_0() {
      // Given
      doReturn(RESTAURANT_DETAILS_RESPONSE_2.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_2, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals(0, result.getRating(), 0);
   }

   @Test
   public void openingHoursResponse_null_should_return_opening_status_not_available() {
      // Given
      doReturn(RESTAURANT_DETAILS_RESPONSE_2.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_2, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals(NOT_AVAILABLE, result.getOpeningStatus());
   }

   @Test
   public void restaurant_open_then_openingStatus_should_return_open_until_closing_time() {
      // Given
      doReturn(FAKE_DATE_TIME).when(dateTimeProvider).getNow();
      doReturn(RESTAURANT_DETAILS_RESPONSE_4.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_4, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals("Ouvert jusqu'à 15h00", result.getOpeningStatus());
   }

   @Test
   public void restaurant_closed_then_openingStatus_should_return_closed_and_opens_at() {
      // Given
      doReturn(FAKE_DATE_TIME).when(dateTimeProvider).getNow();
      doReturn(RESTAURANT_DETAILS_RESPONSE_5.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_5, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals("Fermé ⋅ Ouvre à 10h00 lun.", result.getOpeningStatus());
   }

   @Test
   public void restaurant_closed_then_openingStatus_should_return_closed_and_opens_same_day_next_week() {
      // Given
      doReturn(FAKE_DATE_TIME).when(dateTimeProvider).getNow();
      doReturn(RESTAURANT_DETAILS_RESPONSE_6.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_6, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals("Fermé ⋅ Ouvre à 10h00 dim.", result.getOpeningStatus());
   }

   @Test
   public void restaurant_closed_and_periods_null_should_return_closed_without_next_opening_time() {
      // Given
      doReturn(FAKE_DATE_TIME).when(dateTimeProvider).getNow();
      doReturn(RESTAURANT_DETAILS_RESPONSE_7.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_7, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals("Fermé", result.getOpeningStatus());
   }

   @Test
   public void restaurant_open_and_periods_null_should_return_open_without_closing_time() {
      // Given
      doReturn(FAKE_DATE_TIME).when(dateTimeProvider).getNow();
      doReturn(RESTAURANT_DETAILS_RESPONSE_8.getPlaceId()).when(savedStateHandle).get("restaurantId");
      initViewModel();
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE_8, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      // Then
      assertEquals("Ouvert", result.getOpeningStatus());
   }

   // region IN
   private void initViewModel() {
      viewModel = new DetailsViewModel(application, restaurantDetailsRepository, savedStateHandle, workmateRepository, restaurantImageMapper, dateTimeProvider);
   }
   // endregion IN

   // region OUT
   @NonNull
   private DetailsViewState getDefaultDetailsViewState() {
      return new DetailsViewState(
          RESTAURANT_DETAILS_RESPONSE_1.getPlaceId(),
          "fakeImageUrl",
          RESTAURANT_DETAILS_RESPONSE_1.getName(),
          3,
          true,
          RESTAURANT_DETAILS_RESPONSE_1.getFormattedAddress(),
          "Ouvert",
          RESTAURANT_DETAILS_RESPONSE_1.getFormattedPhoneNumber(),
          RESTAURANT_DETAILS_RESPONSE_1.getWebsite(),
          false,
          false,
          false,
          new ArrayList<>()
      );
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
          null
      );
   }
   // endregion OUT
}