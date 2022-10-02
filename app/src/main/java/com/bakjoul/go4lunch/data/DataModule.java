package com.bakjoul.go4lunch.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.api.RestaurantApi;
import com.bakjoul.go4lunch.data.repository.PermissionRepository;
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

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

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
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    @Provides
    public RestaurantApi provideRestaurantApi(@NonNull Retrofit retrofit) {
        return retrofit.create(RestaurantApi.class);
    }
}
