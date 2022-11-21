package com.bakjoul.go4lunch.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bakjoul.go4lunch.data.api.RestaurantApi;
import com.bakjoul.go4lunch.data.location.LocationPermissionRepository;
import com.bakjoul.go4lunch.data.user.UserRepositoryImplementation;
import com.bakjoul.go4lunch.domain.user.UserRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DataBindingModule {

    @Singleton
    @Binds
    public abstract UserRepository bindsUserRepository(UserRepositoryImplementation implementation);
}
