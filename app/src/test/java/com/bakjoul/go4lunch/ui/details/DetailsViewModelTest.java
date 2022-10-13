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

   private static final RestaurantDetailsResponse RESTAURANT_DETAILS_RESPONSE = new RestaurantDetailsResponse(
       "RESTAURANT_DETAILS_RESPONSE_ID",
       "RESTAURANT_DETAILS_RESPONSE_NAME",
       5,
       10,
       "RESTAURANT_DETAILS_RESPONSE_ADDRESS",
       new OpeningHoursResponse(true, null, null),
       new ArrayList<>(Collections.singletonList(new PhotoResponse("fakePhotoReference"))),
       "RESTAURANT_DETAILS_RESPONSE_PHONE_NUMBER",
       "RESTAURANT_DETAILS_RESPONSE_WEBSITE"
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
      doReturn(RESTAURANT_DETAILS_RESPONSE.getPlaceId()).when(savedStateHandle).get("restaurantId");
      doReturn("fakeImageUrl").when(restaurantImageMapper).getImageUrl("fakePhotoReference", true);

      viewModel = new DetailsViewModel(application, restaurantDetailsRepository, savedStateHandle, restaurantImageMapper);
   }

   @Test
   public void nominal_case() {
      detailsResponseLiveData.setValue(new DetailsResponse(RESTAURANT_DETAILS_RESPONSE, "OK"));

      // When
      DetailsViewState result = LiveDataTestUtil.getValueForTesting(viewModel.getDetailsViewStateLiveData());

      assertEquals(getDefaultDetailsViewState(), result);
   }

   // region OUT
   @NonNull
   private DetailsViewState getDefaultDetailsViewState() {
      return new DetailsViewState(
          RESTAURANT_DETAILS_RESPONSE.getPlaceId(),
          "fakeImageUrl",
          RESTAURANT_DETAILS_RESPONSE.getName(),
          3,
          true,
          RESTAURANT_DETAILS_RESPONSE.getFormattedAddress(),
          "Ouvert",
          RESTAURANT_DETAILS_RESPONSE.getFormattedPhoneNumber(),
          RESTAURANT_DETAILS_RESPONSE.getWebsite(),
          false,
          new ArrayList<>()
      );
   }
   // endregion OUT
}