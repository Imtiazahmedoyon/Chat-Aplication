package com.daffodil.diuchat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.daffodil.diuchat.utils.AndroidUtil;
import com.daffodil.diuchat.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupUsername, signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Adjust system UI padding for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        signupButton.setOnClickListener(view -> {
            if (!isConnectedToInternet()) {
                AndroidUtil.showToast(SignUpActivity.this, "No Internet Connection. Please check your connection and try again.");
                return;
            }

            String username = signupUsername.getText().toString().trim();
            String email = signupEmail.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();

            if (username.isEmpty()) {
                signupUsername.setError("Username cannot be empty");
                return;
            }
            if (email.isEmpty()) {
                signupEmail.setError("Email cannot be empty");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signupEmail.setError("Please enter a valid email");
                return;
            }
            if (password.isEmpty()) {
                signupPassword.setError("Password cannot be empty");
                return;
            }
            if (password.length() < 6) {
                signupPassword.setError("Password must be at least 6 characters long");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            signupButton.setEnabled(false);
            signupButton.setText("Signing up...");

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");

                if (task.isSuccessful()) {
                    String userId = auth.getCurrentUser().getUid();

                    FirebaseUtil.saveUserDetails(userId, username, email, new FirebaseUtil.FirebaseCallback() {
                        @Override
                        public void onSuccess() {
                            AndroidUtil.showToast(SignUpActivity.this, "SignUp Successful. Please verify your email.");
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            AndroidUtil.showToast(SignUpActivity.this, "Failed to send verification email.");
                                        }
                                    });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            AndroidUtil.showToast(SignUpActivity.this, "Failed to save user details.");
                        }
                    });

                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        errorMessage = "This email is already registered. Please use a different email.";
                    }
                    AndroidUtil.showToast(SignUpActivity.this, "SignUp Failed:");
                }
            });
        });

        loginRedirectText.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
