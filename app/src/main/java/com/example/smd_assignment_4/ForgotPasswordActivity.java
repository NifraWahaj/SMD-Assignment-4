package com.example.smd_assignment_4;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button buttonResetPassword;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        inputEmail = findViewById(R.id.input_email);
        buttonResetPassword = findViewById(R.id.button_reset_password);
        progressBar = findViewById(R.id.progress_bar);

        // Set up Reset Password button listener
        buttonResetPassword.setOnClickListener(view -> resetPassword());
    }

    private void resetPassword() {
        String email = inputEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(ForgotPasswordActivity.this, "Processing request...", Toast.LENGTH_SHORT).show();

        // Firebase password reset
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Ensure this is always reached
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset email sent!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after success
                    } else {
                        // Log the error for debugging
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(ForgotPasswordActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                    Toast.makeText(ForgotPasswordActivity.this, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
