package com.bakjoul.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facebook.AccessToken;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (savedInstanceState == null) {
            if (!isLoggedIn) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_layout, LoginFragment.newInstance())
                    .commitNow();
            }
        }
    }
}