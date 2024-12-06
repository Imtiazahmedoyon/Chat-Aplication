package com.daffodil.diuchat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.daffodil.diuchat.utils.AndroidUtil;  // Import the AndroidUtil class

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText, forgotPassword;
    private Button loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            if (!isConnectedToInternet()) {
                AndroidUtil.showToast(LoginActivity.this, "No Internet Connection. Please check your connection and try again.");
                return;
            }

            String email = loginEmail.getText().toString().trim();
            String pass = loginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                loginEmail.setError("Email cannot be empty");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginEmail.setError("Please enter a valid email");
            } else if (TextUtils.isEmpty(pass)) {
                loginPassword.setError("Password cannot be empty");
            } else if (pass.length() < 6) {
                loginPassword.setError("Password must be at least 6 characters long");
            } else {
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null && !user.isEmailVerified()) {
                                AndroidUtil.showToast(LoginActivity.this, "Please verify your email before logging in.");
                                auth.signOut();
                            } else {
                                AndroidUtil.showToast(LoginActivity.this, "Login Successful");
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            String errorMessage = "Login failed. Please check your Email and Password.";
                            if (e.getMessage().contains("The password is invalid")) {
                                errorMessage = "Incorrect password. Please try again.";
                            } else if (e.getMessage().contains("There is no user record")) {
                                errorMessage = "No account found with this email.";
                            } else if (e.getMessage().contains("The email address is badly formatted")) {
                                errorMessage = "Please enter a valid email address.";
                            }

                            AndroidUtil.showToast(LoginActivity.this, errorMessage);
                        });
            }
        });

        // Redirect to sign-up activity
        signupRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        // Redirect to forgot password activity
        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
