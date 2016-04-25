package com.android.movieReviews;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;
import android.app.Dialog;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.view.Menu;
import android.app.ActionBar;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.app.SearchManager;
import android.content.Context;
import android.widget.SearchView;
//import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.lang.ClassNotFoundException;
import java.io.IOException;


public class InduvidualReviewActivity extends Activity implements OnClickListener {

  private SearchResultFragment searchFrag;
  private FragmentTransaction ft;

  private View frameView;

  private MenuItem searchItem;

  private String TAG = MovieActivity.class.getSimpleName();

  NetworkImageView movPoster;
  ImageButton userPhoto, rateIt, bLogo;
  TextView movName, movDesc, userName, userReview;
  RatingBar movRating, userRating;
  Button writeReview;
  LinearLayout linearLayout1, linearLayout2;

  String mName, mDesc, mPosterUrl, mRating, uName, uReview, uPhoto, uRating, uEmail;
  int searchCounter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.induvidualreview);
    setupVars();

    frameView = (View) findViewById(R.id.frame_frag_container);
    if(frameView.isShown()) {
      frameView.setVisibility(View.GONE);
    }

    final ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.actionbar_custom);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    bLogo = (ImageButton) findViewById(R.id.bImgLogo);
    bLogo.setOnClickListener(this);

    Bundle reviewDataBundleR = getIntent().getExtras();
    mName = reviewDataBundleR.getString("movie_title");
    mDesc = reviewDataBundleR.getString("movie_desc");
    mPosterUrl = reviewDataBundleR.getString("movie_poster");
    mRating = reviewDataBundleR.getString("movie_rating");
    uName = reviewDataBundleR.getString("user_name");
    uReview = reviewDataBundleR.getString("user_review");
    uPhoto = reviewDataBundleR.getString("user_photo");
    uRating = reviewDataBundleR.getString("user_rating");
    uEmail = reviewDataBundleR.getString("user_email");

    movName.setText(mName);
    movDesc.setText(mDesc);

    if(null != mPosterUrl) {
      ImageLoader mImageLoader = VolleyApplication.getInstance().getImageLoader();
      movPoster.setImageUrl(mPosterUrl, mImageLoader);
    }

    movRating.setRating(Float.parseFloat(mRating));

    userName.setText(uName);
    userReview.setText(uReview);
    Resources res1 = getResources();
    int resId1 = res1.getIdentifier(uPhoto, "drawable", this.getPackageName());
    userPhoto.setImageResource(resId1);
    userRating.setRating(Float.parseFloat(uRating));

    searchFrag = new SearchResultFragment();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);

    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    searchItem = menu.findItem(R.id.search);

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

      @Override
      public boolean onQueryTextChange(String newText) {
        searchCounter = 0;

        if(!frameView.isShown()) {
          frameView.setVisibility(View.VISIBLE);
        }
        ft = getFragmentManager().beginTransaction();
        if(searchFrag.isAdded()) {
          ft.show(searchFrag);
        }else {
          ft.add(R.id.frame_frag_container, searchFrag);
        }
        ft.commit();

        try {
          fetchSearchResults(newText);
        }catch(IOException e) {
          //e.printStackTrace();
        }
        return true;
      }

      @Override
      public boolean onQueryTextSubmit(String query) {
        searchCounter = 0;
        if(!frameView.isShown()) {
          frameView.setVisibility(View.VISIBLE);
        }
        ft = getFragmentManager().beginTransaction();
        if(searchFrag.isAdded()) {
          ft.show(searchFrag);
        }else {
          ft.add(R.id.frame_frag_container, searchFrag);
        }
        ft.commit();
        try {
          fetchSearchResults(query);
        }catch(IOException e) {
          //e.printStackTrace();
        }

        return true;
      }
    };
    searchView.setOnQueryTextListener(queryTextListener);

    final SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {

      @Override
      public boolean onClose() {
        ft = getFragmentManager().beginTransaction();
        if(searchFrag.isVisible()) {
          ft.hide(searchFrag);
        }
        ft.commit();
        if(frameView.isShown()) {
          frameView.setVisibility(View.GONE);
        }
        return false;
      }
    };
    searchView.setOnCloseListener(closeListener);

    return true;
  }

  @Override
  public void onPause() {
    super.onPause();

  }

  @Override
  public void onBackPressed() {
    if(!searchFrag.isVisible() && !frameView.isShown() && !searchItem.isActionViewExpanded()) {
      super.onBackPressed();
    }else {
      if(searchItem.isActionViewExpanded()) {
        searchItem.collapseActionView();
      }
      if(searchFrag.isVisible()) {
        ft = getFragmentManager().beginTransaction();
        ft.hide(searchFrag);
        ft.commit();
      }
      if(frameView.isShown()) {
        frameView.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void onClick(View v) {
    switch(v.getId()) {
      case R.id.bImgLogo:
        try {
          Bundle mainBundleS = new Bundle();
          mainBundleS.putString("login_email", uEmail);

          Class mainClass = Class.forName("com.android.movieReviews.MainActivity");
          Intent mainIntent = new Intent(InduvidualReviewActivity.this, mainClass);
          mainIntent.putExtras(mainBundleS);
          mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(mainIntent);
        }catch(ClassNotFoundException e) {
          //e.printStackTrace();
        }
        break;
      case R.id.bWriteReview:
        passDataAndOpenWriteReview(uEmail, mPosterUrl, mName, mRating, mDesc);
        break;
      case R.id.bImgRateIt:
        boolean didItWork = true;
        try {
          String user_email = uEmail;
          String movie_name = mName;
          String user_rating = String.valueOf(movRating.getRating());

          AllRatingsReviewsInfo reviewInfo = new AllRatingsReviewsInfo(this);
          reviewInfo.open();
          reviewInfo.createEntry(user_email, movie_name, user_rating, null);
          reviewInfo.close();
        }catch(Exception e) {
          //e.printStackTrace();
          didItWork = false;
          String error = e.toString();
          Dialog d = new Dialog(this);
          TextView tv = new TextView(this);
          tv.setText("Error storing info. Please try again.");
          d.setContentView(tv);
          d.show();
        }finally {
          if(didItWork) {
            Toast.makeText(InduvidualReviewActivity.this, "Rating has been saved!", Toast.LENGTH_SHORT).show();
          }
          movRating.setRating(Float.parseFloat(mRating));
        }
      break;
      case R.id.linearLayout1:
        if(searchItem.isActionViewExpanded()) {
          searchItem.collapseActionView();
        }
        if(searchFrag.isVisible()) {
          ft = getFragmentManager().beginTransaction();
          ft.hide(searchFrag);
          ft.commit();
        }
        if(frameView.isShown()) {
          frameView.setVisibility(View.GONE);
        }
      break;
      case R.id.linearLayout2:
        if(searchItem.isActionViewExpanded()) {
          searchItem.collapseActionView();
        }
        if(searchFrag.isVisible()) {
          ft = getFragmentManager().beginTransaction();
          ft.hide(searchFrag);
          ft.commit();
        }
        if(frameView.isShown()) {
          frameView.setVisibility(View.GONE);
        }
      break;
    }
  }

  public void fetchSearchResults(String query) throws IOException {

    InputStream is = this.getResources().getAssets().open("imdb_250_list.txt");
    InputStreamReader ir = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(ir);

    String line = "";
    searchCounter = 0;
    while((line = br.readLine()) != null) {
      if((line.toLowerCase()).startsWith(query.toLowerCase()) && !query.equals("")) {
        //Log.e(TAG, "Entered 'if' loop of fetchSearchResults(String query)");
        //Log.e(TAG, "line : " + line);
        //Log.e(TAG, "query : " + query);
        line = line.trim();
        line = line.replaceAll("\\s", "%20");
        String movieName = line;
        String url = "http://www.omdbapi.com/?t=" + movieName + "&y=&plot=short&r=json";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
        new Response.Listener<JSONObject>() {

          @Override
          public void onResponse(JSONObject response) {
            try {
              String mName = "";
              String mPoster = "";
              String mDesc = "";
              String mRating = "";

              JSONObject movObj = response;

              if(!(movObj.getString("Title").equals("N/A"))) {
                mName = movObj.getString("Title");
              }
              if(!(movObj.getString("Poster").equals("N/A"))) {
                mPoster = movObj.getString("Poster");
              }
              if(!(movObj.getString("Plot").equals("N/A"))) {
                mDesc = movObj.getString("Plot");
              }
              if(!(movObj.getString("imdbRating").equals("N/A"))) {
                mRating = movObj.getString("imdbRating");
              }else {
                mRating = "0.0";
              }

              if(!mName.equals("") && !mPoster.equals("")) {
                Movie m = new Movie(mName, mDesc, mPoster, mRating);
                searchFrag.addToSearchList(searchCounter, m);
                searchCounter++;
              }
            }catch(Exception e) {
              //Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }
          }
        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError error) {
            //Log.e(TAG, "Server error: " + error.getMessage());
          }
        });
        try {
          VolleyApplication.getInstance().addToRequestQueue(req);
        }catch(Exception e) {
          //Log.e(TAG, "addToRequestQueue error: " + e.getMessage());
        }
      }else {
        try {
          searchCounter = 0;
          String noResults = "No Results found";
          Movie m = new Movie(noResults, null, null, null);
          searchFrag.addToSearchList(searchCounter, m);
        }catch(Exception e) {
          //Log.e(TAG, "NoResultsFound Error: " + e.getMessage());
        }
      }
    }
  }

  public void passDataAndOpenWriteReview(String uEmail, String moviePoster, String movieName,
                                         String movieRating, String movieDesc) {
    Bundle writeReviewBundleS = new Bundle();
    writeReviewBundleS.putString("user_email", uEmail);
    writeReviewBundleS.putString("movie_poster", mPosterUrl);
    writeReviewBundleS.putString("movie_name", movieName);
    writeReviewBundleS.putString("movie_rating", movieRating);
    writeReviewBundleS.putString("movie_desc", movieDesc);

    try {
      Class writeReviewClass = Class.forName("com.android.movieReviews.WriteReviewActivity");
      Intent iWriteReview = new Intent(InduvidualReviewActivity.this, writeReviewClass);
      iWriteReview.putExtras(writeReviewBundleS);
      startActivity(iWriteReview);
    }catch(ClassNotFoundException e) {
      String error = e.toString();
      Dialog d = new Dialog(this);
      TextView tv = new TextView(this);
      tv.setText("Unable to open next screen. Please try again.");
      d.setContentView(tv);
      d.show();
    }
  }

  public void setupVars() {
    movPoster = (NetworkImageView) findViewById(R.id.nwImgMoviePoster);
    userPhoto = (ImageButton) findViewById(R.id.bImgUserPhoto);
    movName = (TextView) findViewById(R.id.tvMovieName);
    movDesc = (TextView) findViewById(R.id.tvMovieDesc);
    userName = (TextView) findViewById(R.id.tvUserName);
    userReview = (TextView) findViewById(R.id.tvUserReview);
    movRating = (RatingBar) findViewById(R.id.movRatingBar);
    userRating = (RatingBar) findViewById(R.id.userRatingBar);
    writeReview = (Button) findViewById(R.id.bWriteReview);
    writeReview.setOnClickListener(this);
    rateIt = (ImageButton) findViewById(R.id.bImgRateIt);
    rateIt.setOnClickListener(this);
    linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
    linearLayout1.setOnClickListener(this);
    linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
    linearLayout2.setOnClickListener(this);
  }
}
