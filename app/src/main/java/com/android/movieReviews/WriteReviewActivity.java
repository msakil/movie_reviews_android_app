package com.android.movieReviews;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.ActionBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class WriteReviewActivity extends Activity implements OnClickListener {

  ImageButton rateMovie, bLogo;
  ImageView userPhoto;
  NetworkImageView movPoster;
  TextView movName, movDesc, userName;
  RatingBar movRating, userRating;
  Button done;
  EditText writeReview;
  String mPosterUrl, mName, mRating, mDesc, uName, uEmail, uPhoto;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.writereview);
    setUpVars();

    final ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.actionbar_custom);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    bLogo = (ImageButton) findViewById(R.id.bImgLogo);
    bLogo.setOnClickListener(this);

    Bundle writeReviewBundleR = getIntent().getExtras();
    uEmail = writeReviewBundleR.getString("user_email");
    mPosterUrl = writeReviewBundleR.getString("movie_poster");
    mName = writeReviewBundleR.getString("movie_name");
    mRating = writeReviewBundleR.getString("movie_rating");
    mDesc = writeReviewBundleR.getString("movie_desc");

    if(null != mPosterUrl) {
      ImageLoader mImageLoader = VolleyApplication.getInstance().getImageLoader();
      movPoster.setImageUrl(mPosterUrl, mImageLoader);
    }

    movName.setText(mName);
    movDesc.setText(mDesc);
    movRating.setRating(Float.parseFloat(mRating));
  }

  @Override
  public void onPause() {
    super.onPause();
    //finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void onClick(View v) {
    switch(v.getId()) {
      case R.id.bImgLogo:
        try {
          Bundle mainBundleS = new Bundle();
          mainBundleS.putString("login_email", uEmail);

          Class mainClass = Class.forName("com.android.movieReviews.MainActivity");
          Intent mainIntent = new Intent(WriteReviewActivity.this, mainClass);
          mainIntent.putExtras(mainBundleS);
          mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(mainIntent);
        }catch(ClassNotFoundException e) {
          //e.printStackTrace();
        }
        break;
      case R.id.bImgRateIt:
        //uEmail, mName, userRating.getRating
        boolean didItWork = true;
        try{
          String rating = String.valueOf(userRating.getRating());
          AllRatingsReviewsInfo reviewInfo = new AllRatingsReviewsInfo(this);
          reviewInfo.open();
          reviewInfo.createEntry(uEmail, mName, rating, null);
          reviewInfo.close();
        }catch(Exception e) {
          didItWork = false;
          String error = e.toString();
          Dialog d = new Dialog(this);
          TextView tv = new TextView(this);
          //tv.setText(error);
          tv.setText("Unable to save info. Please try again.");
          d.setContentView(tv);
          d.show();
        }finally {
          if(didItWork) {
            Toast.makeText(WriteReviewActivity.this, "Rating has been saved!", Toast.LENGTH_SHORT).show();
          }
        }
        break;
      case R.id.bDone:
        String uRating = String.valueOf(userRating.getRating());
        String uReview = writeReview.getText().toString();
        String uPhoto = "userone";
        boolean didItWork1 = true;

        try {
          AllRatingsReviewsInfo reviewInfo = new AllRatingsReviewsInfo(this);
          reviewInfo.open();
          reviewInfo.createEntry(uEmail, mName, uRating, uReview);
          reviewInfo.close();
        }catch(Exception e) {
          //e.printStackTrace();
          didItWork1 = false;
          String error = e.toString();
          Dialog d = new Dialog(this);
          TextView tv = new TextView(this);
          //tv.setText(error);
          tv.setText("Unable to save info. Please try again.");
          d.setContentView(tv);
          d.show();
        }finally {
          if(didItWork1) {
            Toast.makeText(WriteReviewActivity.this, String.valueOf(userRating.getRating()), Toast.LENGTH_SHORT).show();
            try {
              Bundle indRevDataBundleS = new Bundle();
              indRevDataBundleS.putString("user_name", uName);
              indRevDataBundleS.putString("user_review", uReview);
              indRevDataBundleS.putString("user_rating", uRating);
              indRevDataBundleS.putString("user_photo", uPhoto);
              indRevDataBundleS.putString("user_email", uEmail);
              indRevDataBundleS.putString("movie_title", mName);
              indRevDataBundleS.putString("movie_desc", mDesc);
              indRevDataBundleS.putString("movie_poster", mPosterUrl);
              indRevDataBundleS.putString("movie_rating", mRating);

              Class induvidualReviewClass = Class.forName("com.android.movieReviews.InduvidualReviewActivity");
              Intent iReview = new Intent(WriteReviewActivity.this, induvidualReviewClass);
              iReview.putExtras(indRevDataBundleS);
              startActivity(iReview);
            }catch(ClassNotFoundException e) {
              String error = e.toString();
              Dialog d = new Dialog(this);
              TextView tv = new TextView(this);
              //tv.setText(error);
              tv.setText("Unable to open next screen. Please try again.");
              d.setContentView(tv);
              d.show();
            }finally {
              finish();
            }
          }
        }
        break;
    }
  }

  public void addListenerOnRatingBar() {
    userRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
      public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        //Toast.makeText(WriteReviewActivity.this, String.valueOf(userRating.getRating()), Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void setUpVars() {
    rateMovie = (ImageButton)findViewById(R.id.bImgRateIt);
    rateMovie.setOnClickListener(this);
    movPoster = (NetworkImageView) findViewById(R.id.nwImgMoviePoster);
    userPhoto = (ImageView) findViewById(R.id.imgUserPhoto);
    movName = (TextView) findViewById(R.id.tvMovieName);
    movDesc = (TextView) findViewById(R.id.tvMovieDesc);
    userName = (TextView) findViewById(R.id.tvUserName);
    movRating = (RatingBar) findViewById(R.id.movRatingBar);
    userRating = (RatingBar) findViewById(R.id.userRatingBar);
    done = (Button) findViewById(R.id.bDone);
    done.setOnClickListener(this);
    writeReview = (EditText) findViewById(R.id.etWriteReview);
    addListenerOnRatingBar();
  }
}
