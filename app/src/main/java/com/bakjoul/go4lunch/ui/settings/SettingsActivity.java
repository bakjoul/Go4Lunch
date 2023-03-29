package com.bakjoul.go4lunch.ui.settings;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivitySettingsBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SettingsViewModel viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        setToolbar();

        viewModel.getSettingsViewStateLiveData().observe(this, viewState ->
            binding.settingsSwitchNotification.setChecked(viewState.isLunchReminderEnabled())
        );

        binding.settingsSwitchNotification.setOnCheckedChangeListener((compoundButton, isChecked) ->
            viewModel.onNotificationSwitchChanged(isChecked)
        );
    }

    private void setToolbar() {
        Toolbar toolbar = binding.settingsToolbar;
        toolbar.setTitle(R.string.settings_toolbar_title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setBackgroundColor(getColor(R.color.primaryColor));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}