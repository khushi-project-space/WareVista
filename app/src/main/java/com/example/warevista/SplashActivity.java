package com.example.warevista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoSplash = findViewById(R.id.videoSplash);
        videoSplash.setVisibility(View.VISIBLE);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.warevista_intro);

        videoSplash.setVideoURI(uri);

        videoSplash.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            mp.setVolume(0f, 0f);
            videoSplash.start();
        });

        videoSplash.setOnCompletionListener(mp -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        });

        videoSplash.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Video Error: " + what + " / " + extra, Toast.LENGTH_LONG).show();

            // Fallback to Login
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();

            return true;
        });
    }
}