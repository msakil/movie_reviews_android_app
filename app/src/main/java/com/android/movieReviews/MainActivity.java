package com.android.movieReviews;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.Intent;
import android.app.Dialog;
import android.widget.EditText;
import android.view.View.OnTouchListener;
import android.widget.RatingBar;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
//import android.util.Log;
import android.view.ViewGroup;
import android.view.MenuInflater;
import android.view.Menu;
import android.app.Activity;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.SearchView;
import android.app.SearchManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.lang.ClassNotFoundException;
import java.lang.Math;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import android.support.library21.custom.SwipeRefreshLayoutBottom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements OnClickListener, SwipeRefreshLayoutBottom.OnRefreshListener {

  private ListView listView;
  private MovieAdapter adapter;
  private List<Movie> movieList;
  private SwipeRefreshLayoutBottom mSwipeRefreshLayout;

  private String movieName = "";
  private String apiUrl = "";
  private String TAG = MainActivity.class.getSimpleName();

  private ListView searchListView;
  private SearchObjectAdapter searchAdapter;

  private SearchResultFragment searchFrag;
  private FragmentTransaction ft;

  private View frameView;
  //private View parentView;
  private View swipeView;

  private MenuItem searchItem;

  int counter;
  int lineCount;
  String loginEmail = "";

  int searchCounter;

  ImageButton bLogo;
  TextView tvUserName;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mainactivity);
    setupVars();

    frameView = (View) findViewById(R.id.frame_frag_container);
    frameView.setVisibility(View.GONE);

    final ActionBar actionBar = getActionBar();
    actionBar.setCustomView(R.layout.actionbar_custom);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    bLogo = (ImageButton) findViewById(R.id.bImgLogo);
    bLogo.setOnClickListener(this);

    Bundle loginOrMovieBundleR = getIntent().getExtras();
    loginEmail = loginOrMovieBundleR.getString("login_email");
    String userName = "";
    try {
      UserInfo uInfo = new UserInfo(this);
      uInfo.open();
      userName = uInfo.getFirstName(loginEmail);
      uInfo.close();
    }catch(Exception e) {
      //e.printStackTrace();
    }
    if(null != userName) {
      tvUserName.setText("Hi " + userName + ",");
    }else {
      tvUserName.setText("Hi there,");
    }

    MovieInfo movInfo = new MovieInfo(MainActivity.this);

    listView = (ListView) findViewById(R.id.listView);
    listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
        if(!searchFrag.isVisible() && !frameView.isShown()) {
          String mName = "";
          String mDesc = "";
          String mPoster = "";
          String mRating = "";

          Movie m = movieList.get(pos);
          mName = m.getMovieName();
          mDesc = m.getMovieDesc();
          mPoster = m.getMoviePoster();
          mRating = m.getMovieRating();

          passDataAndOpenMovie(mName, mDesc, mPoster, mRating);
        }else {
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

    movieList = new ArrayList<>();
    adapter = new MovieAdapter(this, movieList);
    listView.setAdapter(adapter);

    lineCount = 0;
    counter = 0;
    try {
      fetchMovies();
    }catch(IOException e) {
      //e.printStackTrace();
    };

    searchFrag = new SearchResultFragment();
    searchFrag.setLoginEmail(loginEmail);
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
        if(searchFrag.isVisible()) {
          ft = getFragmentManager().beginTransaction();
          ft.hide(searchFrag);
          ft.commit();
        }

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

    String title = "";
    String desc = "";
    String movPosterName = "";
    String movRating = "";

    switch(v.getId()) {
      case R.id.bImgLogo:
        Intent mainIntent = getIntent();
        finish();
        startActivity(mainIntent);
    }
  }

  @Override
  public void onRefresh() {
    mSwipeRefreshLayout.setRefreshing(true);
    setProgressBarIndeterminateVisibility(true);
    try {
      fetchMovies();
    }catch(IOException e) {
      //e.printStackTrace();
    }finally {
      mSwipeRefreshLayout.setRefreshing(false);
      setProgressBarIndeterminateVisibility(false);
    }
  }

  //Code block to fetch and populate movies via api dynamically
  //Movies batch size is 10 movies in one call
  public void fetchMovies() throws IOException {
    int runCount = 0;
    boolean reqLineReached = false;

    InputStream is = this.getResources().getAssets().open("imdb_250_list.txt");
    InputStreamReader ir = new InputStreamReader(is);
    BufferedReader buffReader = new BufferedReader(ir);
    String line = "";

    for(int i=0; i <= lineCount; i++) {
      buffReader.readLine();
      reqLineReached = true;
    }

    if(!(lineCount > 0)) {
      reqLineReached = true;
    }

    if(reqLineReached) {
      while(((line = buffReader.readLine()) != null) && (runCount <= 10)) {
        line = line.trim();
        line = line.replaceAll("\\s","%20");
        movieName = line;
        String url = "http://www.omdbapi.com/?t=" + movieName + "&y=&plot=short&r=json";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                                      new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                          //Log.e(TAG, response.toString());

                                          try {
                                            String mName = "";
                                            String mDesc = "";
                                            String mRating = "";
                                            String mPoster = "";

                                            JSONObject movObj = response;

                                            if(!(movObj.getString("Title")).equals("N/A")) {
                                              mName = movObj.getString("Title");
                                            }else {
                                              mName = null;
                                            }
                                            if(!(movObj.getString("Plot")).equals("N/A")) {
                                              mDesc = movObj.getString("Plot");
                                            }else {
                                              mDesc = null;
                                            }
                                            if(!(movObj.getString("imdbRating")).equals("N/A")) {
                                              mRating = String.valueOf(Math.abs((Float.parseFloat(movObj.getString("imdbRating"))/2)));
                                            }else {
                                              mRating = "0.0";
                                            }
                                            if(!(movObj.getString("Poster")).equals("N/A")) {
                                              mPoster = movObj.getString("Poster");
                                            }else {
                                              mPoster = null;
                                            }

                                            if(mName != null && mDesc != null && mPoster != null) {
                                              Movie m = new Movie(mName, mDesc, mPoster, mRating);
                                              movieList.add(counter, m);
                                              counter++;
                                              adapter.notifyDataSetChanged();
                                            }
                                          }catch(Exception e) {
                                            //Log.e(TAG, "JSON parsing error: " + e.getMessage());
                                          }
                                        }
                                      }, new Response.ErrorListener() {
                                           @Override
                                           public void onErrorResponse(VolleyError error) {
                                             //Log.e(TAG, "Server Error: " + error.getMessage());
                                             //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                           }
                               });
        VolleyApplication.getInstance().addToRequestQueue(req);
        runCount++;
      }
    }
    lineCount = lineCount + 11;
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
        //try-catch setup to solve 'attempt to invoke virtual method' NPE at SearchResultFragment.java line 52
        try {
          searchCounter = 0;
          String noResults = "No Results found";
          //SearchObject so = new SearchObject(noResults, null);
          Movie m = new Movie(noResults, null, null, null);
          searchFrag.addToSearchList(searchCounter, m);
        }catch(Exception e) {
          //Log.e(TAG, "NoResultsFound Error: " + e.getMessage());
        }
      }
    }
  }

  public void passDataAndOpenMovie(String title, String desc, String movPosterName, String movRating) {
    try {

      Bundle movDataBundle = new Bundle();
      movDataBundle.putString("movie_title", title);
      movDataBundle.putString("movie_desc", desc);
      movDataBundle.putString("movie_poster_name", movPosterName);
      movDataBundle.putString("movie_rating", movRating);
      movDataBundle.putString("user_email", loginEmail);

      Class movieActivityClass = Class.forName("com.android.movieReviews.MovieActivity");
      Intent iMovie = new Intent(MainActivity.this, movieActivityClass);
      iMovie.putExtras(movDataBundle);

      startActivity(iMovie);
    }catch(ClassNotFoundException e) {
      String error = e.toString();
      Dialog d = new Dialog(this);
      TextView tv = new TextView(this);
      //tv.setText(error);
      tv.setText("Encountered problems opening next screen. Please try again.");
      d.setContentView(tv);
      d.show();
    }
  }

  public void setupVars() {
    //bLogo = (ImageButton) findViewById(R.id.bImgLogo);
    //bLogo.setOnClickListener(this);
    tvUserName = (TextView) findViewById(R.id.tvUserName);
    mSwipeRefreshLayout = (SwipeRefreshLayoutBottom) findViewById(R.id.swipeRefreshLayout);
    mSwipeRefreshLayout.setOnRefreshListener(this);
  }
}
