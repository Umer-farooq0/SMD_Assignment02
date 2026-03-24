package com.example.mid1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splash extends AppCompatActivity {

    ImageView logo;
    TextView appName;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.splash_logo);
        appName = findViewById(R.id.app_name);
        sPref = getSharedPreferences("user",MODE_PRIVATE);

        // Load animations
        Animation fadeScale = AnimationUtils.loadAnimation(this, R.anim.splash);
        Animation slide = AnimationUtils.loadAnimation(this, R.anim.left_to_right);

        // Apply animations
        logo.startAnimation(fadeScale);
        appName.startAnimation(slide);

        // Move to next screen after delay
        new Handler().postDelayed(() -> {

            boolean isFirstTime = sPref.getBoolean("app.isFirstTime", true);
            boolean isLogin = sPref.getBoolean("user.isLogin", false);

            if (isFirstTime) {
                startActivity(new Intent(splash.this, OnBoarding.class));
            }
            else if (!isLogin) {
                startActivity(new Intent(splash.this, activity_credentials.class));
            }
            else {
                startActivity(new Intent(splash.this, MainActivity2.class));
            }

            finish();

        }, 3000);
    }
}