package com.bakjoul.go4lunch.ui.details;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.DetailsResponse;
import com.bakjoul.go4lunch.data.model.OpeningHoursResponse;
import com.bakjoul.go4lunch.data.model.PhotoResponse;
import com.bakjoul.go4lunch.data.model.RestaurantDetailsResponse;
import com.bakjoul.go4lunch.data.repository.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.ui.utils.RestaurantImageMapper;
import com.bakjoul.go4lunch.utils.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

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
   // endregion Constants

   @Rule
   public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

   private final Application application = Mockito.mock(Application.class);
   private final RestaurantDetailsRepository restaurantDetailsRepository = Mockito.mock(RestaurantDetailsRepository.class);
   private final SavedStateHandle savedStateHandle = Mockito.mock(SavedStateHandle.class);
   private final RestaurantImageMapper restaurantImageMapper = Mockito.mock(RestaurantImageMapper.class);

   private final MutableLiveData<DetailsResponse> detailsResponseLiveData = new MutableLiveData<>();

   private DetailsViewModel viewModel;

   @Before
   public void setUp() {
      given(application.getString(R.string.restaurant_is_open)).willReturn(OPEN);
      given(application.getString(R.string.restaurant_is_closed)).willReturn(CLOSED);
      given(application.getString(R.string.information_not_available)).willReturn(NOT_AVAILABLE);
      given(application.getString(R.string.details_opened_until)).willReturn(UNTIL);
      given(application.getString(R.string.details_time_separator)).willReturn(TIME_SEPARATOR);
      given(application.getString(R.string.details_open_at)).willReturn(OPEN_AT);

      doReturn(detailsResponseLiveData).when(restaurantDetailsRepository).getDetailsResponse(anyString(), anyString());
      doReturn("fakeImageUrl").when(restaurantImageMapper).getImageUrl("fakePhotoReference", true);
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

   // region IN
   private void initViewModel() {
      viewModel = new DetailsViewModel(application, restaurantDetailsRepository, savedStateHandle, restaurantImageMapper);
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
          null
      );
   }
   // endregion OUT
}