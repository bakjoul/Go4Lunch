package com.bakjoul.go4lunch.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivityChatBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    public static void navigate(String workmateId, Activity sourceActivity) {
        Bundle arg = new Bundle();
        arg.putString("workmateId", workmateId);
        Intent intent = new Intent(sourceActivity, ChatActivity.class);
        intent.putExtras(arg);
        sourceActivity.startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Hides keyboard on click outside
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setToolbar();
        binding.chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onClick: ");
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = binding.chatToolbar;
        toolbar.setTitle("Chat");
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