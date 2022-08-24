package com.bakjoul.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bakjoul.go4lunch.databinding.FragmentLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private ActivityResultLauncher activityResultLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoginBinding binding = FragmentLoginBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();
        LoginButton fbLoginButton = binding.loginButtonFacebook;
        fbLoginButton.setPermissions("email", "public_profile");
        fbLoginButton.setFragment(this);

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFbAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d(TAG, "facebook:onError", e);
            }
        });

/*        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getString(R.string.google_server_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build())
            .build();*/

        binding.loginButtonFacebook.setOnClickListener(view ->
            LoginManager.getInstance().logInWithReadPermissions(
                requireActivity(),
                callbackManager,
                Collections.singletonList("public_profile")
            )
        );

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    private void handleFbAccessToken(@NonNull AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
            });
    }
}