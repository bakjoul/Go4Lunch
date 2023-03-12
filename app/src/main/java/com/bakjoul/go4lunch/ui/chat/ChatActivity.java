package com.bakjoul.go4lunch.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.ActivityChatBinding;

import java.util.Collections;
import java.util.List;

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

        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        RecyclerView recyclerView = binding.chatRecyclerView;
        //ChatAdapter adapter = new ChatAdapter();
        ChatAdapterSimple adapter = new ChatAdapterSimple();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        setToolbar();
        binding.chatSendBtn.setOnClickListener(v -> {
            if (binding.chatInputEdit.getText() != null) {
                viewModel.sendMessage(binding.chatInputEdit.getText().toString());
                binding.chatInputEdit.setText("");
            }
        });

        viewModel.getChatViewStateLiveData().observe(this, viewState -> {
                List<ChatMessageItemViewState> messages = viewState.getMessageItemViewStates();
                Collections.reverse(messages);
                adapter.submitList(messages);
                linearLayoutManager.scrollToPosition(messages.size() - 1);

                recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        linearLayoutManager.scrollToPosition(0);
                        recyclerView.removeOnLayoutChangeListener(this);
                    }
                });
            }
        );
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