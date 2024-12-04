package com.daffodil.diuchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.daffodil.diuchat.utils.AndroidUtil;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton, cancelButton;
    private FirebaseAuth auth;
    private ProgressBar progressBar;  // Declare the progress bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailBox);
        resetPasswordButton = findViewById(R.id.btnReset);
        cancelButton = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);

        // Reset password button click listener
        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // Check if email is empty
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email cannot be empty");
                return;
            }

            // Check if email is valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email address");
                return;
            }

            // Disable the reset button to prevent multiple clicks
            resetPasswordButton.setEnabled(false);

            // Show progress bar while processing
            progressBar.setVisibility(View.VISIBLE);

            // Send password reset email using Firebase
            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> {
                        // Hide progress bar and enable the reset button
                        progressBar.setVisibility(View.GONE);
                        resetPasswordButton.setEnabled(true);

                        // Show success message
                        AndroidUtil.showToast(ForgotPasswordActivity.this, "Password reset email sent. Please check your inbox.");

                        // Redirect user to LoginActivity
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();  // Close the current activity to prevent the user from returning to it

                    })
                    .addOnFailureListener(e -> {
                        // Hide progress bar and enable the reset button
                        progressBar.setVisibility(View.GONE);
                        resetPasswordButton.setEnabled(true);

                        // Show error message if Firebase fails
                        String errorMessage = e.getMessage();
                        AndroidUtil.showToast(ForgotPasswordActivity.this, "Failed to send reset email: " + errorMessage);
                        e.printStackTrace(); // Print error stack trace for debugging
                    });
        });

        // Cancel button click listener (navigate back to LoginActivity)
        cancelButton.setOnClickListener(v -> {
            // Navigate back to the LoginActivity
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Close the current activity
        });
    }
}