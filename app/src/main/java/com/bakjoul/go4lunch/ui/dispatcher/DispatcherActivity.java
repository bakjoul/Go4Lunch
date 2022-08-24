package com.bakjoul.go4lunch.ui.dispatcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.ui.LoginActivity;
import com.bakjoul.go4lunch.ui.MainActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DispatcherViewModel viewModel = new ViewModelProvider(this).get(DispatcherViewModel.class);

        viewModel.getViewActionSingleLiveEvent().observe(this, dispatcherViewAction -> {
            switch (dispatcherViewAction) {
                case GO_TO_CONNECT_SCREEN:
                    startActivity(new Intent(DispatcherActivity.this, LoginActivity.class));
                    finish();
                    break;
                case GO_TO_MAIN_SCREEN:
                    startActivity(new Intent(DispatcherActivity.this, MainActivity.class));
                    finish();
                    break;
            }
        });
    }
}