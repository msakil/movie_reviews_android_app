package com.android.movieReviews;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Dialog;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.app.SearchManager;
import android.content.Context;
import android.widget.SearchView;
//import android.util.Log;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import android.support.library21.custom.SwipeRefreshLayoutBottom;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import android.support.library21.custom.SwipeRefreshLayoutBottom;

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

public class MovieActivity extends Activity implements OnClickListener, SwipeRefreshLayoutBottom.OnRefreshListener {

  private ListView listView;
  private ReviewAdapter adapter;
  private List<Review> reviewList;
  private SwipeRefreshLayoutBottom swipeRefreshLayoutBottom;

  private SearchResultFragment searchFrag;
  private FragmentTransaction ft;

  private View frameView;

  private MenuItem searchItem;

  private String TAG = MovieActivity.class.getSimpleName();

  TextView movieName, movieDesc;
  String title, desc, movPosterUrl, movRating, user_email;
  NetworkImageView movPoster;
  RatingBar movRatingBar;
  ImageButton bRateMov, bLogo;
  Button bWriteReview;
  LinearLayout linearLayout;

  boolean isVisible = false;
  int indexCounter, searchCounter;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.movieactivity);
    setupVars();

    frameView = (View) findViewById(R.id.frame_frag_container);
    frameView.setVisibility(View.GONE);

    final ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.actionbar_custom);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    bLogo = (ImageButton) findViewById(R.id.bImgLogo);
    bLogo.setOnClickListener(this);

    Bundle movDataBundleR = getIntent().getExtras();
    title = movDataBundleR.getString("movie_title");
    desc = movDataBundleR.getString("movie_desc");
    movPosterUrl = movDataBundleR.getString("movie_poster_name");
    movRating = movDataBundleR.getString("movie_rating");
    user_email = movDataBundleR.getString("user_email");

    movieName.setText(title);
    movieDesc.setText(desc);
    if(null != movPosterUrl) {
      ImageLoader mImageLoader = VolleyApplication.getInstance().getImageLoader();
      movPoster.setImageUrl(movPosterUrl, mImageLoader);
    }
    if(null != movRating && !movRating.equals("")) {
      movRatingBar.setRating(Float.parseFloat(movRating));
    }

    reviewList = new ArrayList<>();
    adapter = new ReviewAdapter(this, reviewList);
    listView.setAdapter(adapter);

    indexCounter = 0;
    fetchReviews();

    listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
        if(!searchFrag.isVisible() && !frameView.isShown()) {
          String rName = "";
          String rReview = "";
          String rRating = "";
          String rPhoto = "";
          String uEmail = user_email;

          Review r = reviewList.get(pos);
          rName = r.getRname();
          rReview = r.getRreview();
          rRating = r.getRrating();
          rPhoto = r.getRphoto();

          passDataAndOpenUserReview(rName, rReview, rRating, rPhoto, uEmail);
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
    });

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
          mainBundleS.putString("login_email", user_email);

          Class mainClass = Class.forName("com.android.movieReviews.MainActivity");
          Intent mainIntent = new Intent(MovieActivity.this, mainClass);
          mainIntent.putExtras(mainBundleS);
          mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(mainIntent);
        }catch(ClassNotFoundException e) {
          //e.printStackTrace();
        }
        break;
      case R.id.bImgRateIt:
        boolean didItWork = true;
        try {
          String uEmail = user_email;
          String mName = title;
          String uRating = "";
          AllRatingsReviewsInfo reviewsInfo = new AllRatingsReviewsInfo(this);
          reviewsInfo.open();
          uRating = String.valueOf(movRatingBar.getRating());
          reviewsInfo.createEntry(uEmail, mName, uRating, null);
          reviewsInfo.close();
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
            Toast.makeText(MovieActivity.this, "Rating has been saved!", Toast.LENGTH_SHORT).show();
          }
          movRatingBar.setRating(Float.parseFloat(movRating));
        }
        break;
      case R.id.bWriteReview:
        passDataAndOpenWriteReview(movPosterUrl, title, movRating, desc, user_email);
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
    }
  }

  @Override
  public void onRefresh() {
    swipeRefreshLayoutBottom.setRefreshing(true);
    setProgressBarIndeterminateVisibility(true);
    try{
      if(indexCounter <= 5) {
        fetchReviews();
      }
    }catch(Exception e) {
      //e.printStackTrace();
    }finally {
      swipeRefreshLayoutBottom.setRefreshing(false);
      setProgressBarIndeterminateVisibility(false);
    }
  }

  public void fetchReviews() {
    String rName = "";
    String rPhoto = "";
    String rRating = "";
    String rReview = "";
    String rEmail = "";
    int dbPos;
    Random rand = new Random();

    for(int i=0; i<=4; i++) {
      dbPos = rand.nextInt(8) + 1;

      try {
        AllRatingsReviewsInfo reviewInfo = new AllRatingsReviewsInfo(this);
        reviewInfo.open();
        rEmail = reviewInfo.getEmail(dbPos);
        rRating = reviewInfo.getRating(dbPos);
        rReview = reviewInfo.getReview(dbPos);
        reviewInfo.close();

        UserInfo uInfo = new UserInfo(this);
        uInfo.open();
        rName = uInfo.getFirstName(rEmail);
        rPhoto = uInfo.getPhoto(rEmail);
        uInfo.close();

        Review r = new Review(rName, rPhoto, rRating, rReview);
        reviewList.add(indexCounter, r);
        indexCounter++;
      }catch(Exception e) {
        //e.printStackTrace();
      }
    }

    adapter.notifyDataSetChanged();
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

  public void passDataAndOpenUserReview(String name, String review, String rating, String photo, String email) {

    //below read 'user' as 'review(er)'
    //not changed cos at the time of commenting the sys has already become too
    //complicated to change
    Bundle reviewDataBundleS = new Bundle();
    reviewDataBundleS.putString("user_name", name);
    reviewDataBundleS.putString("user_review", review);
    reviewDataBundleS.putString("user_rating", rating);
    reviewDataBundleS.putString("user_photo", photo);
    reviewDataBundleS.putString("user_email", email);
    reviewDataBundleS.putString("movie_title", title);
    reviewDataBundleS.putString("movie_desc", desc);
    reviewDataBundleS.putString("movie_poster", movPosterUrl);
    reviewDataBundleS.putString("movie_rating", movRating);

    try {
      Class reviewActivityClass = Class.forName("com.android.movieReviews.InduvidualReviewActivity");
      Intent iReview = new Intent(MovieActivity.this, reviewActivityClass);
      iReview.putExtras(reviewDataBundleS);
      startActivity(iReview);
    }catch(ClassNotFoundException e) {
      String error = e.toString();
      Dialog d = new Dialog(this);
      TextView tv = new TextView(this);
      //tv.setText(error);
      tv.setText("Unable to open next screen. Please try again.");
      d.setContentView(tv);
      d.show();
    }
  }

  public void passDataAndOpenWriteReview(String movPoster, String movName, String movRating, String movDesc, String uEmail) {

    Bundle writeReviewBundleS = new Bundle();
    writeReviewBundleS.putString("movie_poster", movPoster);
    writeReviewBundleS.putString("movie_name", movName);
    writeReviewBundleS.putString("movie_rating", movRating);
    writeReviewBundleS.putString("movie_desc", movDesc);
    writeReviewBundleS.putString("user_email", uEmail);

    try {
      Class writeReviewClass = Class.forName("com.android.movieReviews.WriteReviewActivity");
      Intent iWriteReview = new Intent(MovieActivity.this, writeReviewClass);
      iWriteReview.putExtras(writeReviewBundleS);
      startActivity(iWriteReview);
    }catch(ClassNotFoundException e) {
      String error = e.toString();
      Dialog d = new Dialog(this);
      TextView tv = new TextView(this);
      //tv.setText(error);
      tv.setText("Unable to open next screen. Please try again.");
      d.setContentView(tv);
      d.show();
    }
  }

  public void setupVars() {
    movieName = (TextView) findViewById(R.id.tvMovieName);
    movieDesc = (TextView) findViewById(R.id.tvMovieDesc);
    movPoster = (NetworkImageView) findViewById(R.id.nwImgMoviePoster);
    movRatingBar = (RatingBar) findViewById(R.id.movRatingBar);
    bRateMov = (ImageButton) findViewById(R.id.bImgRateIt);
    bRateMov.setOnClickListener(this);
    bWriteReview = (Button) findViewById(R.id.bWriteReview);
    bWriteReview.setOnClickListener(this);
    listView = (ListView) findViewById(R.id.listView);
    swipeRefreshLayoutBottom = (SwipeRefreshLayoutBottom) findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayoutBottom.setOnRefreshListener(this);
    linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);
    linearLayout.setOnClickListener(this);
  }
}
