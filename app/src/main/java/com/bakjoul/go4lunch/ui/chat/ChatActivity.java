package com.bakjoul.go4lunch.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bakjoul.go4lunch.databinding.ActivityChatBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    @NonNull
    public static Intent navigate(
        Context context,
        String workmateId,
        String photoUrl,
        String username
    ) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("workmateId", workmateId);
        intent.putExtra("photoUrl", photoUrl);
        intent.putExtra("username", username);
        return intent;
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
                Glide.with(this)
                    .load(viewState.getPhotoUrl())
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            binding.chatReceiverInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(resource, null, null, null);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
                binding.chatReceiverInfo.setText(viewState.getWorkmateUsername());

                adapter.submitList(viewState.getChatItemViewStates());

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


    private void setInputOnKeyListener(ChatViewModel viewModel) {
        binding.chatInputEdit.setOnKeyListener((view, keyCode, event) -> {
            // Directly sends message when hitting Enter key on keyboards
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                handleMessageSent(viewModel);
                return true;
            }
            // Hides soft keyboard on Escape key pressed
            else if (keyCode == KeyEvent.KEYCODE_ESCAPE && event.getAction() == KeyEvent.ACTION_DOWN) {
                hideSoftKeyboard(view);
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
        // Hides keyboard on click outside of input box
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view == binding.chatInputLinearLayout.findFocus()) {
                Rect rect = new Rect();
                binding.chatInputLinearLayout.getGlobalVisibleRect(rect);
                int x = (int) ev.getRawX();
                int y = (int) ev.getRawY();
                if (!rect.contains(x, y)) {
                    hideSoftKeyboard(view);
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideSoftKeyboard(@NonNull View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setToolbar() {
        binding.chatToolbar.setTitle("");
        setSupportActionBar(binding.chatToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
}