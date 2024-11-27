package com.example.smd_assignment_4;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashLogo = findViewById(R.id.splash_logo);

        // Translate Animation
        TranslateAnimation translateAnimation = new TranslateAnimation(
                0, 0, -500, 0); // Move from top to center
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);

        // Scale Animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1f, 1.2f, // From 100% to 120% in X
                1f, 1.2f, // From 100% to 120% in Y
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot in the center
        scaleAnimation.setDuration(500);
        scaleAnimation.setStartOffset(1000); // Start after the translate animation ends

        // Start the animations
        splashLogo.startAnimation(translateAnimation);
        splashLogo.startAnimation(scaleAnimation);

        // Navigate to Login Screen after the animation
        new Handler().postDelayed(() -> {
            // Check if user is already authenticated
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // User is logged in, redirect to MainActivity
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                // User is not logged in, redirect to LoginActivity
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
            finish();
        }, 2000); // 2-second delay



    }
}
