package com.bakjoul.go4lunch.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.api.RestaurantSearchService;
import com.bakjoul.go4lunch.data.repository.PermissionRepository;
import com.bakjoul.go4lunch.data.repository.RestaurantRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class DataModule {

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public FusedLocationProviderClient provideFusedLocationProviderClient(@ApplicationContext Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @Provides
    @Singleton
    public PermissionRepository providePermissionRepository(@ApplicationContext Context context) {
        return new PermissionRepository(context);
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
            .baseUrl(RestaurantRepository.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    @Provides
    public RestaurantSearchService provideRestaurantSearchService(@NonNull Retrofit retrofit) {
        return retrofit.create(RestaurantSearchService.class);
    }
}
