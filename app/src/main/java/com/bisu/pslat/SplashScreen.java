package com.bisu.pslat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // on below line we are configuring our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);

        int timelimit= 2000;

        /** CountDownTimer starts with 1 minutes and every onTick is 1 second */
        new CountDownTimer(timelimit, 1000) {

            public void onTick(long millisUntilFinished) {

                long finishedSeconds = timelimit - millisUntilFinished;
                int total = (int) (((float)finishedSeconds / (float)timelimit) * 100.0);
                bar.setProgress(total);
            }

            public void onFinish() {
                // DO something when time is up
                bar.setProgress(100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashScreen.this, Login.class);

                        // on below line we are
                        // starting a new activity.
                        startActivity(i);

                        // on the below line we are finishing
                        // our current activity.
                        finish();
                    }
                }, 500);
            }
        }.start();
    }
}