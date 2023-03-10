package com.bakjoul.go4lunch.data;

import com.bakjoul.go4lunch.data.autocomplete.AutocompleteRepositoryImplementation;
import com.bakjoul.go4lunch.data.chat.ChatRepositoryImplementation;
import com.bakjoul.go4lunch.data.details.RestaurantDetailsRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.GpsLocationRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.LocationModeRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.LocationPermissionRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.MapLocationRepositoryImplementation;
import com.bakjoul.go4lunch.data.restaurants.RestaurantRepositoryImplementation;
import com.bakjoul.go4lunch.data.settings.SettingsRepositoryImplementation;
import com.bakjoul.go4lunch.data.user.UserRepositoryImplementation;
import com.bakjoul.go4lunch.data.workmates.WorkmateRepositoryImplementation;
import com.bakjoul.go4lunch.domain.autocomplete.AutocompleteRepository;
import com.bakjoul.go4lunch.domain.chat.ChatRepository;
import com.bakjoul.go4lunch.domain.details.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.domain.location.GpsLocationRepository;
import com.bakjoul.go4lunch.domain.location.LocationModeRepository;
import com.bakjoul.go4lunch.domain.location.LocationPermissionRepository;
import com.bakjoul.go4lunch.domain.location.MapLocationRepository;
import com.bakjoul.go4lunch.domain.restaurants.RestaurantRepository;
import com.bakjoul.go4lunch.domain.settings.SettingsRepository;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.bakjoul.go4lunch.domain.workmate.WorkmateRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DataBindingModule {

    @Singleton
    @Binds
    public abstract UserRepository bindUserRepository(UserRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract GpsLocationRepository bindGpsLocationRepository(GpsLocationRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract LocationModeRepository bindLocationModeRepository(LocationModeRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract LocationPermissionRepository bindLocationPermissionRepository(LocationPermissionRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract MapLocationRepository bindMapLocationRepository(MapLocationRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract RestaurantDetailsRepository bindRestaurantDetailsRepository(RestaurantDetailsRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract RestaurantRepository bindRestaurantRepository(RestaurantRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract WorkmateRepository bindWorkmateRepository(WorkmateRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract AutocompleteRepository bindAutocompleteRepository(AutocompleteRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract SettingsRepository bindSettingsRepository(SettingsRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract ChatRepository bindChatRepository(ChatRepositoryImplementation implementation);
}
