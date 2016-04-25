package com.android.movieReviews;

import android.app.Activity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.content.Intent;

import java.lang.Thread;
import java.lang.InterruptedException;

public class Splash extends Activity {
    /** Called when the activity is first created. */

    MediaPlayer mySong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //mySong = MediaPlayer.create(Splash.this, R.raw.umbrella);
        //mySong.start();
        Thread timer = new Thread() {
          public void run() {
            try {
              sleep(5000);
            }catch(InterruptedException e) {
              //e.printStackTrace();
            }finally {
              Intent openHelloActivity = new Intent("com.android.movieReviews.LOGIN");
              startActivity(openHelloActivity);
            }
          }
        };
        timer.start();
    }

    @Override
    public void onPause() {
      super.onPause();
      //mySong.release();
      finish();
    }
}
