package com.bakjoul.go4lunch.ui.dispatcher;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.bakjoul.go4lunch.domain.auth.IsUserAuthenticatedUseCase;
import com.bakjoul.go4lunch.utils.SingleLiveEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DispatcherViewModel extends ViewModel {

    @NonNull
    private final SingleLiveEvent<DispatcherViewAction> viewActionSingleLiveEvent = new SingleLiveEvent<>();

    @Inject
    public DispatcherViewModel(@NonNull IsUserAuthenticatedUseCase isUserAuthenticatedUseCase) {
        if (isUserAuthenticatedUseCase.invoke()) {
            viewActionSingleLiveEvent.setValue(DispatcherViewAction.GO_TO_MAIN_SCREEN);
        } else {
            viewActionSingleLiveEvent.setValue(DispatcherViewAction.GO_TO_CONNECT_SCREEN);
        }
    }

    @NonNull
    public SingleLiveEvent<DispatcherViewAction> getViewActionSingleLiveEvent() {
        return viewActionSingleLiveEvent;
    }
}
