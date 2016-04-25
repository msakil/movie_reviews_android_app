package com.android.movieReviews;

import android.app.Fragment;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.app.Activity;
import android.content.Intent;
import android.app.Dialog;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;
import java.lang.ClassNotFoundException;

public class SearchResultFragment extends Fragment {

  private ListView listView;
  private SearchObjectAdapter adapter;
  //private List<SearchObject> searchList;
  private List<Movie> searchList;
  private Activity activity;
  private String loginEmail;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.search_result_fragment, container, false);
    listView = (ListView) view.findViewById(R.id.fragListView);
    activity = getActivity();
    listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
        String mName = "";
        String mDesc = "";
        String mRating = "";
        String mPoster = "";

        Movie movItem = searchList.get(pos);
        mName = movItem.getMovieName();
        mDesc = movItem.getMovieDesc();
        mPoster = movItem.getMoviePoster();
        mRating = movItem.getMovieRating();

        if(!mName.equals("No Results found") && null != mName && null != mPoster) {
          passDataAndOpenMovie(mName, mDesc, mPoster, mRating);
        }
      }
    });
    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    searchList = new ArrayList<>();
    adapter = new SearchObjectAdapter(getActivity(), searchList);
    listView.setAdapter(adapter);
  }

  public void addToSearchList(int counter, Movie m) {
    if(searchList == null) {
      searchList = new ArrayList<>();
    }
    if(counter == 0) {
      searchList.clear();
    }
    searchList.add(counter, m);
    if(adapter == null) {
      adapter = new SearchObjectAdapter(getActivity(), searchList);
      listView.setAdapter(adapter);
    }
    adapter.notifyDataSetChanged();
  }

  public void setLoginEmail(String email) {
    loginEmail = email;
  }

  public void passDataAndOpenMovie(String title, String desc, String movPosterName, String movRating) {
    try {
      Activity activity = getActivity();

      Bundle movDataBundle = new Bundle();
      movDataBundle.putString("movie_title", title);
      movDataBundle.putString("movie_desc", desc);
      movDataBundle.putString("movie_poster_name", movPosterName);
      movDataBundle.putString("movie_rating", movRating);
      movDataBundle.putString("user_email", loginEmail);

      Class movieActivityClass = Class.forName("com.android.movieReviews.MovieActivity");
      Intent iMovie = new Intent(activity, movieActivityClass);
      iMovie.putExtras(movDataBundle);

      startActivity(iMovie);
    }catch(ClassNotFoundException e) {
      String error = e.toString();
      Dialog d = new Dialog(activity);
      TextView tv = new TextView(activity);
      //tv.setText(error);
      tv.setText("Unable to open next screen. Please try again.");
      d.setContentView(tv);
      d.show();
    }
  }
}
