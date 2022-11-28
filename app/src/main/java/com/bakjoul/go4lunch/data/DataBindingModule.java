package com.bakjoul.go4lunch.data;

import com.bakjoul.go4lunch.data.details.RestaurantDetailsRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.GpsLocationRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.LocationModeRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.LocationPermissionRepositoryImplementation;
import com.bakjoul.go4lunch.data.location.MapLocationRepositoryImplementation;
import com.bakjoul.go4lunch.data.restaurants.RestaurantRepositoryImplementation;
import com.bakjoul.go4lunch.data.user.UserRepositoryImplementation;
import com.bakjoul.go4lunch.domain.details.RestaurantDetailsRepository;
import com.bakjoul.go4lunch.domain.location.GpsLocationRepository;
import com.bakjoul.go4lunch.domain.location.LocationModeRepository;
import com.bakjoul.go4lunch.domain.location.LocationPermissionRepository;
import com.bakjoul.go4lunch.domain.location.MapLocationRepository;
import com.bakjoul.go4lunch.domain.restaurants.RestaurantRepository;
import com.bakjoul.go4lunch.domain.user.UserRepository;

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
    public abstract UserRepository bindsUserRepository(UserRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract GpsLocationRepository bindsGpsLocationRepository(GpsLocationRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract LocationModeRepository bindsLocationModeRepository(LocationModeRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract LocationPermissionRepository bindsLocationPermissionRepository(LocationPermissionRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract MapLocationRepository bindsMapLocationRepository(MapLocationRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract RestaurantDetailsRepository bindsRestaurantDetailsRepository(RestaurantDetailsRepositoryImplementation implementation);

    @Singleton
    @Binds
    public abstract RestaurantRepository bindsRestaurantRepository(RestaurantRepositoryImplementation implementation);
}
