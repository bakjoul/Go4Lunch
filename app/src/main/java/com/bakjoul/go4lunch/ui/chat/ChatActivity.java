package com.bakjoul.go4lunch.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        ChatAdapter adapter = new ChatAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        binding.chatRecyclerView.setAdapter(adapter);
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        setToolbar();
        setSendButtonOnClickListener(viewModel);
        setInputOnKeyListener(viewModel);

        viewModel.getChatViewStateLiveData().observe(this, viewState -> {
                adapter.submitList(viewState.getMessageItemViewStates());

                binding.chatRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        linearLayoutManager.scrollToPosition(0);
                        binding.chatRecyclerView.removeOnLayoutChangeListener(this);
                    }
                });
            }
        );
    }

    private void setSendButtonOnClickListener(ChatViewModel viewModel) {
        binding.chatSendBtn.setOnClickListener(v -> handleMessageSent(viewModel));
    }

    // Directly sends message when hitting Enter key on keyboards
    private void setInputOnKeyListener(ChatViewModel viewModel) {
        binding.chatInputEdit.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                handleMessageSent(viewModel);
                return true;
            }
            return false;
        });
    }

    private void handleMessageSent(ChatViewModel viewModel) {
        if (binding.chatInputEdit.getText() != null) {
            viewModel.sendMessage(binding.chatInputEdit.getText().toString());
            binding.chatInputEdit.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        // Hides keyboard on click outside of input
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view.isFocused() && view instanceof EditText) {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                int x = (int) ev.getRawX();
                int y = (int) ev.getRawY();
                if (!rect.contains(x, y)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setToolbar() {
        binding.chatToolbar.setTitle("Chat");
        setSupportActionBar(binding.chatToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
}